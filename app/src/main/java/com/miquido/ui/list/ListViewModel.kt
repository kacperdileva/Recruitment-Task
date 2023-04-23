package com.miquido.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miquido.data.searchDetails.searchDetails
import com.miquido.data.searchDetails.searchDetailsRepository
import com.miquido.data.unsplash.UnsplashRepository
import com.miquido.data.unsplash.UnsplashResponse
import com.miquido.utils.ResultEvent
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.request
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.lang.reflect.InvocationTargetException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val unsplashRepository: UnsplashRepository,
    private val searchDetailsRepository: searchDetailsRepository
) : ViewModel() {
    private var searchJob: Job? = null
    private var resultJob: Job? = null

    private var _currentResponse = MutableLiveData<UnsplashResponse>()
    val currentResponse: LiveData<UnsplashResponse> get() = _currentResponse


    private var _currentSearchDetails = MutableLiveData<searchDetails>()
    val currentSearchDetails: LiveData<searchDetails> get() = _currentSearchDetails

    private val resultChannel = Channel<ResultEvent>()
    val resultEvent = resultChannel.receiveAsFlow()

    init {
        initializeImages()

    }

    private fun initializeImages() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _currentSearchDetails.value =
                searchDetailsRepository.getDetailsFromDatabase()
                    ?: searchDetails(page = DEFAULT_PAGE_INDEX)
            val response = unsplashRepository.getSearchFromDB()
            response?.let {
                _currentResponse.value = it
            } ?: currentSearchDetails.value?.let { searchImages(it) }
        }
    }

    fun searchImageFromMenu(query: String) = viewModelScope.launch {
        val searchDetails = searchDetails(query = query, page = DEFAULT_PAGE_INDEX)
        searchDetailsRepository.deleteDetailsFromDatabase()
        searchDetailsRepository.insertToDatabase(searchDetails)
        _currentSearchDetails.value = searchDetails
    }


    fun searchImages(searchDetails: searchDetails) {
        val query = searchDetails.query
        val page = searchDetails.page
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            val call = unsplashRepository.getSearchResultFromAPI(
                query = query,
                page = page,
                perPage = PAGE_SIZE
            )
            call.request { apiResponse ->
                when (apiResponse) {

                    is ApiResponse.Success -> {
                        viewModelScope.launch {
                            apiResponse.data?.let {
                                if (it.results.isNotEmpty()) {
                                    _currentResponse.value = it
                                    unsplashRepository.deleteResponseFromDB()
                                    unsplashRepository.insertResponseToDB(it)
                                    searchDetailsRepository.insertToDatabase(
                                        searchDetails(
                                            page = page,
                                            query = query
                                        )
                                    )
                                } else {
                                    handleError(NOT_FOUND)
                                }

                            } ?: kotlin.run {
                                handleError(NOT_FOUND)
                            }
                        }
                    }

                    is ApiResponse.Failure.Error -> {
                        handleError(NOT_FOUND)
                    }

                    is ApiResponse.Failure.Exception -> {
                        handleExceptions(apiResponse.exception)
                    }

                }
            }
        }
    }

    fun getImagesFromDb() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            val result = unsplashRepository.getSearchFromDB()
            result?.let {
                _currentResponse.value = it
            }
        }
    }

    fun deleteImagesFromDb() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            unsplashRepository.deleteResponseFromDB()
        }
    }

    private fun handleError(msg: String) {
        resultJob?.cancel()
        resultJob = viewModelScope.launch {
            resultChannel.send(ResultEvent.Error(msg))
        }
    }

    private fun handleExceptions(exception: Throwable) {
        when (exception) {
            is UnknownHostException -> {
                val msg = "No Item's found\nNo Internet Connection!"
                handleError(msg)
            }
            is InvocationTargetException,
            is SocketTimeoutException -> {
                val msg = "No Item's found"
                handleError(msg)
            }

            else -> {
                val msg = "No Item's found\nException: ${exception.message}"
                handleError(msg)
                throw exception
            }
        }
    }

    fun incrementCurrentPage() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            val searchDetails =
                searchDetailsRepository.getDetailsFromDatabase()
                    ?: searchDetails(page = DEFAULT_PAGE_INDEX)
            searchDetails.let {
                val x = it.copy(page = it.page + 1)
                searchDetailsRepository.deleteDetailsFromDatabase()
                searchDetailsRepository.insertToDatabase(x)
                _currentSearchDetails.value = x
            }
        }
    }


    companion object {
        val PAGE_SIZE = 20
        val DEFAULT_PAGE_INDEX = 1
        val NOT_FOUND = "No Item's found"
    }
}
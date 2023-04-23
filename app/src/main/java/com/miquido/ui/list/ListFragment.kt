package com.miquido.ui.list

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.miquido.R
import com.miquido.data.unsplash.UnsplashPhoto
import com.miquido.data.unsplash.UnsplashResponse
import com.miquido.databinding.FragmentListBinding
import com.miquido.utils.ResultEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ListFragment : Fragment(R.layout.fragment_list), ListAdapter.OnItemClickListener {
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<ListViewModel>()

    private var adapter = ListAdapter(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentListBinding.bind(view)

        initializeUI()

        addObservers()

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            collectEvents()
        }

        setHasOptionsMenu(true)
    }

    private suspend fun collectEvents() {
        viewModel.resultEvent.collect { event ->
            when (event) {
                is ResultEvent.Success -> {
                    //We dont do anything here becouse we are observing result from View Model
                    Log.d("ResultEvent", "Success")
                }

                is ResultEvent.Error -> {
                    binding.apply {
                        listProgressBar.visibility = View.INVISIBLE
                        listRecyclerView.visibility = View.INVISIBLE
                        listTxtError.apply {
                            text = event.msg
                            visibility = View.VISIBLE
                        }
                    }
                    Log.d("ResultEvent", "Error")
                }
            }
        }
    }

    private fun initializeUI() {
        binding.apply {
            listProgressBar.visibility = View.VISIBLE
            listRecyclerView.adapter = adapter
            listRecyclerView.setHasFixedSize(true)
            listFabRetryBtn.apply {
                setOnClickListener {
                    animate().rotationBy(ROTATION_DEGREE).duration = FAB_ANIM_DURATION
                    viewModel.incrementCurrentPage()
                    listProgressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun addObservers() {
        viewModel.currentResponse.observe(viewLifecycleOwner) {
            it?.let {
                updateUI(it)
            }
        }

        viewModel.currentSearchDetails.observe(viewLifecycleOwner) {
            it?.let {
                Log.d("SearchDetails", "Page: ${it.page}, Query: ${it.query}")
                viewModel.searchImages(it)
            }
        }
    }

    private fun updateUI(response: UnsplashResponse) {
        binding.apply {
            listTxtError.visibility = View.INVISIBLE
            listProgressBar.visibility = View.INVISIBLE
            listRecyclerView.visibility = View.VISIBLE
        }

        adapter.submitList(response.results)
    }

    override fun onItemClick(photo: UnsplashPhoto) {
        val action = ListFragmentDirections.actionListFragmentToDetailsFragment(photo)
        findNavController().navigate(action)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_list,menu)

        val searchItem = menu.findItem(R.id.menu_item_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    binding.apply {
                        listRecyclerView.scrollToPosition(0)
                        listProgressBar.visibility= View.VISIBLE
                    }
                    viewModel.searchImageFromMenu(query)
                    searchView.clearFocus()
                }

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    companion object {
        private const val FAB_ANIM_DURATION: Long = 1500
        private const val ROTATION_DEGREE: Float = 360f
    }
}
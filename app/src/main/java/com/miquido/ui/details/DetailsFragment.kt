package com.miquido.ui.details

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.miquido.R
import com.miquido.data.unsplash.UnsplashPhoto
import com.miquido.databinding.FragmentDetailsBinding
import com.miquido.utils.fadeIn
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsFragment : Fragment(R.layout.fragment_details) {
    private val args by navArgs<DetailsFragmentArgs>()
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDetailsBinding.bind(view)
        binding.detailsProgressBar.visibility = View.VISIBLE

        val photo = args.photo
        updateUI(photo)
    }

    private fun updateUI(photo: UnsplashPhoto) {
        binding.apply {
            detailsProgressBar.visibility = View.INVISIBLE
            context?.let {
                Glide.with(it)
                    .load(photo.urls.regular)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.img_not_found)
                    .into(detailsImg)
            }

            detailsDesc.apply {
                isVisible = true
                text = photo.description
            }
            val uri = Uri.parse(photo.user.photoUrl)
            val intent = Intent(Intent.ACTION_VIEW, uri)

            detailsAuthor.apply {
                isVisible = true
                text = "Created by ${photo.user.name} on Unsplash Photo's"
                setOnClickListener {
                    context.startActivity(intent)
                }
                paint.isUnderlineText = true
            }
        }

        animateText()
    }

    private fun animateText() {
        binding.apply {
            detailsDesc.fadeIn()
            detailsAuthor.fadeIn()
        }
    }
}
package com.example.newsline.ui.details

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.newsline.R
import com.example.newsline.databinding.FragmentDetailsBinding
import com.example.newsline.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*


@AndroidEntryPoint
class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private val bundleArgs: DetailsFragmentArgs by navArgs()
    private val viewModel by viewModels<DetailsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val articleArg = bundleArgs.article


        viewModel.favoriteLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    if (it.data == true) {
                        binding.iconFavorite.setImageResource(R.drawable.favorite_icon)
                    }
                    if (it.data == false) {
                        binding.iconFavorite.setImageResource(R.drawable.unfavorite_icon)

                    }

                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        articleArg.let { article ->
            article.urlToImage.let {
                Glide.with(this).load(article.urlToImage).into(binding.headerImage)
            }

            viewModel.favoriteIconCheck(article)

            binding.headerImage.clipToOutline = true
            binding.articleDetailsTitle.text = article.title
            binding.articleDetailsDecriptionText.text = article.description

            binding.articleDetailsButton.setOnClickListener {
                try {
                    Intent()
                        .setAction(Intent.ACTION_VIEW)
                        .addCategory(Intent.CATEGORY_BROWSABLE)
                        .setData(Uri.parse(takeIf { URLUtil.isValidUrl(article.url) }
                            ?.let {
                                article.url
                            } ?: "https://google.com"))
                        .let {
                            ContextCompat.startActivity(requireContext(), it, null)
                        }
                } catch (e: Exception) {
                    Toast.makeText(context,
                        "The device doesn't have any browser to view the document!",
                        Toast.LENGTH_SHORT)
                }
            }



            binding.iconFavorite.setOnClickListener {
                if (viewModel.favoriteLiveData.value?.data == false) {
                    //  binding.iconFavorite.setBackgroundColor(resources.getColor(R.color.red))
                    viewModel.saveFavoriteNews(article)
                }
                if (viewModel.favoriteLiveData.value?.data == true) {
                    //  binding.iconFavorite.setBackgroundColor(resources.getColor(R.color.red))
                    viewModel.deleteFavoriteNews(article)
                }

            }

            binding.iconBack.setOnClickListener {
                findNavController().popBackStack()
            }


        }

    }


}
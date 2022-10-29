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
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.newsline.R
import com.example.newsline.databinding.FragmentDetailsBinding
import com.example.newsline.models.Article
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


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
        var articleArg = bundleArgs.article
        initView(articleArg)
        viewModel.favoriteNews.observe(viewLifecycleOwner) {
            println("Hello DetailsFragment.observe: ${articleArg.favorite} ")
            chekDataAndInitIconFavorite(articleArg)


        }
        articleArg.let { article ->
//            initView(article)
            visiteSiteOnClick(article)
            favoriteIconOnclick(article)

            binding.iconBack.setOnClickListener {
                findNavController().popBackStack()
            }
            binding.iconShare.setOnClickListener {
                shareToOtherApps(article.url)
            }
        }


    }

    private fun initView(article: Article) {
        article.urlToImage.let {
            Glide.with(this).load(article.urlToImage).into(binding.headerImage)
        }
        binding.headerImage.clipToOutline = true
        binding.articleDetailsTitle.text = article.title
        binding.articleDetailsDecriptionText.text = article.description


    }

    private fun chekDataAndInitIconFavorite(article: Article) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            var newArticle = viewModel.checkFavorite(article)

            println("??????????    $newArticle")
            println("newArticle.favorite: " + newArticle.favorite)
            if (newArticle.favorite) {
                binding.iconFavorite.setImageResource(R.drawable.ic_favorite_icon)
            } else {
                binding.iconFavorite.setImageResource(R.drawable.ic_unfavorite_icon)

            }
        }

    }


    private fun visiteSiteOnClick(article: Article) {
        binding.visiteSiteButton.setOnClickListener {
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
    }

    private fun favoriteIconOnclick(article: Article) {
        binding.iconFavorite.setOnClickListener {
            if (!article.favorite) {
                binding.iconFavorite.setImageResource(R.drawable.ic_favorite_icon)
                article.favorite = true
                viewModel.saveFavoriteNews(article)
            } else {
                article.favorite = false
                viewModel.deleteFavoriteNews(article)
                binding.iconFavorite.setImageResource(R.drawable.ic_unfavorite_icon)
            }
        }
    }

    private fun shareToOtherApps(message: String?) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }


}
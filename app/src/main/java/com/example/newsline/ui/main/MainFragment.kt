package com.example.newsline.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsline.R
import com.example.newsline.databinding.FragmentMainBinding
import com.example.newsline.models.Article
import com.example.newsline.ui.adapters.NewsAdapter
import com.example.newsline.utils.Constants
import com.example.newsline.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    lateinit var newsAdapter: NewsAdapter
    private val viewModel by viewModels<MainViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()

        viewModel.newsLiveData.observe(viewLifecycleOwner) { responce ->
            when (responce) {
                is Resource.Success -> {
                    pag_progress_bar.visibility = View.INVISIBLE
                    responce.data?.let {
                        newsAdapter.differ.submitList(it.articles)
                    }
                }
                is Resource.Error -> {
                    pag_progress_bar.visibility = View.VISIBLE
                    responce.data?.let {
                        Log.e("checkData", "MainFragment: error: ${it}")
                    }
                    Toast.makeText(requireContext(), responce.message, Toast.LENGTH_SHORT).show()

                }
                is Resource.Loading -> {
                    pag_progress_bar.visibility = View.VISIBLE
                }
            }
        }
        newsAdapter.setOnItemClickListener {
            val bundle = bundleOf("article" to it)
            view.findNavController().navigate(
                R.id.action_mainFragment_to_detailsFragment,
                bundle
            )
        }

        newsAdapter.setOnIconShareClickListener {
            shareToOtherApps(it.url)
        }


        binding.refreshLayout.setOnRefreshListener {
            viewModel.getNews(Constants.RU)
            refreshLayout.isRefreshing = false
        }


    }

    private fun initAdapter() {
        newsAdapter = NewsAdapter { clickListener -> clickListenerForAdapter(clickListener) }
        news_adapter.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }


    private fun clickListenerForAdapter(article: Article) {

        viewLifecycleOwner.lifecycleScope.launch {

            if (article.favorite) {

                Log.i("Database info", "Article deleted in database")
                article.favorite = false
                viewModel.deleteFavoriteNews(article)


            } else {

                Log.i("Database info", "Article insert in database")
                article.favorite = true
                viewModel.saveFavoriteNews(article)

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
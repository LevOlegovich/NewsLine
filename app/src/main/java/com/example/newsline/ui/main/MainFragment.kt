package com.example.newsline.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.newsline.R
import com.example.newsline.databinding.FragmentMainBinding
import com.example.newsline.ui.adapters.NewsAdapter
import com.example.newsline.utils.Constants
import com.example.newsline.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_main.*

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
        // Inflate the layout for this fragment
        _binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()

        newsAdapter.setOnItemClickListener {
            val bundle = bundleOf("article" to it)
            view.findNavController().navigate(
                R.id.action_mainFragment_to_detailsFragment,
                bundle
            )
        }


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


        binding.refreshLayout.setOnRefreshListener {
            viewModel.getNews(Constants.RU)
            refreshLayout.isRefreshing = false
        }


    }

    private fun initAdapter() {
        newsAdapter = NewsAdapter()
        news_adapter.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }


}
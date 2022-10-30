package com.example.newsline.ui.search

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsline.R
import com.example.newsline.databinding.FragmentSearchBinding
import com.example.newsline.ui.adapters.SearchNewsAdapter
import com.example.newsline.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<SearchViewModel>()

    lateinit var newsAdapter: SearchNewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSearchBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()


        newsAdapter.setOnItemClickListener {
            val bundle = bundleOf("article" to it)
            view.findNavController().navigate(
                R.id.action_searchFragment_to_detailsFragment,
                bundle
            )
        }

        var job: Job? = null
        ed_search.addTextChangedListener { text: Editable? ->
            job?.cancel()
            job = MainScope().launch {
                delay(500L)
                text?.let {
                    if (it.trim().toString().isNotEmpty()) {
                        viewModel.getSearchNews(query = it.toString())
                        Log.d("checkData", "SearchFragment text: ${it.trim()}")
                    } else {
                        job?.cancel()
                        newsAdapter.differ.submitList(emptyList())
                    }
                }
            }
        }

        viewModel.searchNewsLiveData.observe(viewLifecycleOwner)
        { responce ->
            when (responce) {
                is Resource.Success -> {
                    pag_search_progress_bar.visibility = View.INVISIBLE
                    responce.data?.let { newsResponse ->
                        val text =
                            resources.getString(R.string.search) + " ${newsResponse.articles.size}"
                        binding.searchText.text = text
                        newsAdapter.differ.submitList(newsResponse.articles)
                    }
                }
                is Resource.Error -> {
                    responce.data?.let {
                        Log.e("checkData", "SearchFragment: error: ${it}")
                    }
                    Toast.makeText(requireContext(), responce.message, Toast.LENGTH_SHORT).show()

                    pag_search_progress_bar.visibility = View.INVISIBLE
                }
                is Resource.Loading -> {
                    pag_search_progress_bar.visibility = View.VISIBLE
                }
            }
        }


    }


    private fun initAdapter() {
        newsAdapter = SearchNewsAdapter()
        search_news_adapter.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }


}
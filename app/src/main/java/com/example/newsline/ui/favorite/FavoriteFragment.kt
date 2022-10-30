package com.example.newsline.ui.favorite

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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsline.R
import com.example.newsline.databinding.FragmentFavoriteBinding
import com.example.newsline.models.Article
import com.example.newsline.ui.adapters.FavoriteNewsAdapter
import com.example.newsline.ui.dialogfarg.DialogManager
import com.example.newsline.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoriteFragment : Fragment() {


    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<FavoriteViewModel>()
    lateinit var newsAdapter: FavoriteNewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        _binding = FragmentFavoriteBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        setupSwipeListener(binding.newsAdapter)
        newsAdapter.setOnIconShareClickListener {
            shareToOtherApps(it.url)
        }

        newsAdapter.setOnItemClickListener {
            val bundle = bundleOf("article" to it)
            view.findNavController().navigate(
                R.id.action_favoriteFragment_to_detailsFragment,
                bundle
            )
        }
        binding.deleteBtn.setOnClickListener {
            showDialog()
        }

        viewModel.favoriteNewsLiveData.observe(viewLifecycleOwner) { responce ->
            when (responce) {
                is Resource.Success -> {
                    pag_progress_bar.visibility = View.INVISIBLE
                    responce.data?.let {
                        newsAdapter.differ.submitList(it)
                        val text = resources.getString(R.string.saved_news) + " ${it.size}"
                        binding.savedNewsText.text = text


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
            viewModel.getFavoriteNews()
            refreshLayout.isRefreshing = false
        }


    }

    private fun showDialog() {
        DialogManager.showDialog(requireActivity(),
            object : DialogManager.Listener {
                override fun onClick() {
                    viewModel.deleteALLFavoriteNews()
                }

            })
    }


    private fun initAdapter() {
        newsAdapter =
            FavoriteNewsAdapter { clickListener -> clickListenerForAdapter(clickListener) }
        news_adapter.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun setupSwipeListener(rvShopList: RecyclerView) {
        val callback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val article = newsAdapter.differ.currentList[viewHolder.adapterPosition]
                viewModel.deleteFavoriteNews(article)
            }
        }
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(rvShopList)
    }


    private fun clickListenerForAdapter(article: Article) {

        viewLifecycleOwner.lifecycleScope.launch {

            if (article.favorite) {
                Log.i("Database info", "Article deleted in database")
                article.favorite = false
                viewModel.deleteFavoriteNews(article)
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


    override fun onStart() {
        super.onStart()
        viewModel.getFavoriteNews()
    }

}
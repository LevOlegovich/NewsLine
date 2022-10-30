package com.example.newsline.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsline.R
import com.example.newsline.models.Article
import kotlinx.android.synthetic.main.item_article.view.*

class NewsAdapter(
    private val clickListener: (Article) -> Unit,
) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    private var myViewType = TYPE_LOADING

    inner class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private val callback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, callback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_article, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(article.urlToImage).into(article_image)
            article_image.clipToOutline = true
            article_title.text = article.title
            article_date.text = article.publishedAt
            if (getItemViewType(position) == TYPE_LOADING) {
                setIconFavorite(article)
                progressBarInItem.visibility = View.VISIBLE
            }
            if (getItemViewType(position) == TYPE_SUCCESS) {
                setIconFavorite(article)
                progressBarInItem.visibility = View.INVISIBLE
            }


            setOnClickListener {
                onItemClickListener?.let { it(article) }
            }

            icon_favorite.setOnClickListener {
                clickListener(article)
                notifyItemChanged(position)
            }

            iconShare.setOnClickListener {
                onIconShareClickListener?.let { it1 -> it1(article) }
            }
        }


    }

    private fun View.setIconFavorite(article: Article) {
        if (article.favorite) {
            icon_favorite.setImageResource(R.drawable.ic_favorite_icon)


        } else {
            icon_favorite.setImageResource(R.drawable.ic_unfavorite_icon)


        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun getItemViewType(position: Int): Int {
        return myViewType
    }

    private var onItemClickListener: ((Article) -> Unit)? = null
    private var onIconShareClickListener: ((Article) -> Unit)? = null

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnIconShareClickListener(listener: (Article) -> Unit) {
        onIconShareClickListener = listener
    }

    fun setViewType(viewType: Int) {
        myViewType = viewType
    }

    companion object {
        const val TYPE_LOADING = 1

        const val TYPE_SUCCESS = 2

    }


}
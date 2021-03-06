package com.github.grubber.dailytopic.bookshelf.details

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.bindView
import com.github.grubber.dailytopic.R
import com.github.grubber.dailytopic.base.ContentFragment
import com.github.grubber.dailytopic.base.PagingRecyclerViewAdapter
import com.github.grubber.dailytopic.bookshelf.chapter.ChapterActivity
import com.github.grubber.dailytopic.bookshelf.model.Book
import com.github.grubber.dailytopic.bookshelf.model.Chapter
import com.squareup.picasso.Picasso
import java.util.*

/**
 * Created by grubber on 2017/1/22.
 */
class BookshelfDetailsFragment : ContentFragment(), BookshelfDetailsContract.View {
    companion object {
        private const val EXTRA_BOOK = "book"
        private const val EXTRA_URL = "url"

        fun newInstance(book: Book?, url: String?): BookshelfDetailsFragment {
            val fragment = BookshelfDetailsFragment()
            val args = Bundle()
            book?.let {
                args.putParcelable(EXTRA_BOOK, it)
            }
            url?.let {
                args.putString(EXTRA_URL, it)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun createContentView(container: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.fragment_bookshelf_details, container, false)
    }

    private val _book by lazy {
        arguments.getParcelable<Book>(EXTRA_BOOK)
    }
    private val _url by lazy {
        arguments.getString(EXTRA_URL)
    }

    private lateinit var _presenter: BookshelfDetailsContract.Presenter

    private val _recyclerView by bindView<RecyclerView>(R.id.recyclerView)
    private val _adapter by lazy {
        BookshelfDetailsItemAdapter(context, picasso())
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        _recyclerView.layoutManager = LinearLayoutManager(context)
        _recyclerView.adapter = _adapter

        if (_url != null) {
            _presenter.setUrl(_url)
        } else {
            _adapter.headerItem = _book
            _adapter.notifyDataSetChanged()
            _presenter.setUrl(_book.url())
        }
        _presenter.subscribe()
    }

    override fun setContentView(data: Pair<Book, List<Chapter>>) {
        setTitle(data.first.title())
        if (_book == null) {
            _adapter.headerItem = data.first
        }
        _adapter.title = data.first.title()
        _adapter.addItems(data.second, PagingRecyclerViewAdapter.STATE_LOADING_COMPLETED)
        displayContentView()
    }

    override fun setErrorView() {
        displayErrorView()
    }

    override fun setPresenter(presenter: BookshelfDetailsContract.Presenter) {
        _presenter = presenter
    }

    class BookshelfDetailsItemAdapter(private val context: Context,
                                      private val picasso: Picasso) : PagingRecyclerViewAdapter<Chapter, Book>() {
        override fun getLoadCount(): Int {
            // Ignored.
            return 0
        }

        override fun onLoadMore(pageNumber: Int) {
            // Ignored.
        }

        override fun shouldShowLoading(): Boolean {
            return false
        }

        override fun hasFooter(): Boolean {
            return false
        }

        override fun onCreateBasicItemViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return BookshelfDetailsItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_bookshelf_details, parent, false))
        }

        var title: String = ""

        override fun onBindBasicItemView(holder: RecyclerView.ViewHolder, position: Int) {
            holder as BookshelfDetailsItemViewHolder
            val item = getItem(position)
            holder.titleView.text = item.title()
            holder.itemView.setOnClickListener {
                ChapterActivity.start(context, title, getItems() as ArrayList<Chapter>, position)
            }
        }

        override fun hasHeader(): Boolean {
            return true
        }

        override fun onBindHeaderView(holder: RecyclerView.ViewHolder, position: Int) {
            super.onBindHeaderView(holder, position)
            holder as BookshelfDetailsHeaderItemViewHolder
            picasso.load(headerItem!!.image())
                    .fit()
                    .into(holder.coverView)
            holder.titleView.text = headerItem!!.title()
            holder.authorView.text = headerItem!!.author()
        }

        override fun onCreateHeaderViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
            return BookshelfDetailsHeaderItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_bookshelf_details_header, parent, false))
        }

        override fun onRetry(pageNumber: Int) {
            // Ignored.
        }

        class BookshelfDetailsHeaderItemViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
            val coverView by bindView<ImageView>(R.id.coverView)
            val titleView by bindView<TextView>(R.id.titleView)
            val authorView by bindView<TextView>(R.id.authorView)
        }

        class BookshelfDetailsItemViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
            val titleView by bindView<TextView>(R.id.titleView)
        }
    }

    override fun onRetry() {
        _presenter.subscribe()
    }

    override fun onDestroy() {
        _presenter.unsubscribe()
        super.onDestroy()
    }

    override fun getTitle(): String? {
        return _book?.title() ?: ""
    }
}
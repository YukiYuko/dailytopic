package com.github.xtorrent.dailytopic.bookshelf.source

import com.github.xtorrent.dailytopic.bookshelf.model.Book
import com.github.xtorrent.dailytopic.bookshelf.model.BookshelfHeaderImage
import com.github.xtorrent.dailytopic.bookshelf.model.Chapter
import com.github.xtorrent.dailytopic.main.MainRepositoryScope
import rx.Observable
import javax.inject.Inject

/**
 * Created by grubber on 2017/1/18.
 */
@MainRepositoryScope
class BookshelfRepository @Inject constructor(private @LocalBookshelf val localDataSource: BookshelfDataSource,
                                              private @RemoteBookshelf val remoteDataSource: BookshelfDataSource) : BookshelfDataSource {
    override fun getBookshelfList(pageNumber: Int): Observable<Pair<List<BookshelfHeaderImage>?, List<Book>>> {
        return remoteDataSource.getBookshelfList(pageNumber)
    }

    override fun getBookshelfDetails(url: String): Observable<Pair<Book, List<Chapter>>> {
        return remoteDataSource.getBookshelfDetails(url)
    }
}
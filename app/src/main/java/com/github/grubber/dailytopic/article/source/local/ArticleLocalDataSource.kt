package com.github.grubber.dailytopic.article.source.local

import com.github.grubber.dailytopic.article.model.Article
import com.github.grubber.dailytopic.article.source.ArticleDataSource
import com.github.grubber.dailytopic.db.DatabaseManager
import com.github.grubber.dailytopic.db.model.ArticleModel
import rx.Observable
import rx.lang.kotlin.emptyObservable
import rx.lang.kotlin.observable

/**
 * @author Grubber
 */
class ArticleLocalDataSource(private val databaseManager: DatabaseManager) : ArticleDataSource {
    private val _db by lazy {
        databaseManager.database
    }

    override fun getArticle(isRandom: Boolean): Observable<Article> {
        return observable {
            if (!it.isUnsubscribed) {
                try {
                    var article: Article? = null
                    if (isRandom) {
                        it.onNext(article)
                        it.onCompleted()
                    } else {
                        val query = Article.FACTORY.select_row_by_type(if (isRandom) Article.Type.NONE else Article.Type.DAILY)
                        val cursor = _db.rawQuery(query.statement, query.args)
                        cursor.use {
                            while (it.moveToNext()) {
                                article = Article.FACTORY.select_row_by_typeMapper().map(it)
                            }
                        }
                        it.onNext(article)
                        it.onCompleted()
                    }
                } catch (e: Exception) {
                    it.onError(e)
                }
            }
        }
    }

    override fun getArticle(title: String, author: String, type: Article.Type): Observable<Article> {
        return observable {
            if (!it.isUnsubscribed) {
                try {
                    val query = Article.FACTORY.select_row(title, author, type)
                    val cursor = _db.rawQuery(query.statement, query.args)
                    var article: Article? = null
                    cursor.use {
                        while (it.moveToNext()) {
                            article = Article.FACTORY.select_rowMapper().map(it)
                        }
                    }
                    it.onNext(article)
                    it.onCompleted()
                } catch (e: Exception) {
                    it.onError(e)
                }
            }
        }
    }

    override fun deleteArticle(title: String, author: String, type: Article.Type) {
        val delete = ArticleModel.Delete_row(_db, Article.FACTORY)
        delete.bind(title, author, type)
        delete.program.execute()
    }

    override fun saveArticle(article: Article) {
        val insert = ArticleModel.Insert_row(_db, Article.FACTORY)
        insert.bind(article.title(), article.author(), article.content(), article.backgroundImage(), article.type())
        insert.program.execute()
    }

    override fun deleteArticle(type: Article.Type) {
        val delete = ArticleModel.Delete_row_by_type(_db, Article.FACTORY)
        delete.bind(type)
        delete.program.execute()
    }

    override fun getFavouriteArticleList(): Observable<List<Article>> {
        return observable {
            if (!it.isUnsubscribed) {
                try {
                    val data = arrayListOf<Article>()
                    val query = Article.FACTORY.select_row_by_type(Article.Type.FAVOURITE)
                    val cursor = _db.rawQuery(query.statement, query.args)
                    cursor.use {
                        while (it.moveToNext()) {
                            data += Article.FACTORY.select_row_by_typeMapper().map(it)
                        }
                    }
                    it.onNext(data)
                    it.onCompleted()
                } catch (e: Exception) {
                    it.onError(e)
                }
            }
        }
    }

    override fun getFavouriteArticle(_id: Long): Observable<Article> {
        return observable {
            if (!it.isUnsubscribed) {
                try {
                    var article: Article? = null
                    val query = Article.FACTORY.select_row_by_id(_id, Article.Type.FAVOURITE)
                    val cursor = _db.rawQuery(query.statement, query.args)
                    cursor.use {
                        while (it.moveToNext()) {
                            article = Article.FACTORY.select_row_by_idMapper().map(it)
                        }
                    }
                    it.onNext(article)
                    it.onCompleted()
                } catch (e: Exception) {
                    it.onError(e)
                }
            }
        }
    }

    override fun createArticle(title: String, author: String, content: String, deliver: String, source: String): Observable<String> {
        // Ignored.
        return emptyObservable()
    }
}
package com.github.xtorrent.dailytopic

import android.content.Context
import android.support.multidex.MultiDexApplication
import com.github.xtorrent.dailytopic.article.source.ArticleRepositoryComponent
import com.github.xtorrent.dailytopic.article.source.ArticleRepositoryModule
import com.github.xtorrent.dailytopic.book.source.BookRepositoryComponent
import com.github.xtorrent.dailytopic.book.source.BookRepositoryModule
import com.github.xtorrent.dailytopic.core.di.*
import com.github.xtorrent.dailytopic.db.DatabaseManager
import com.github.xtorrent.dailytopic.utils.DeviceUtils
import com.github.xtorrent.dailytopic.utils.ToastHelper
import com.squareup.picasso.Picasso
import timber.log.Timber
import javax.inject.Inject
import kotlin.properties.Delegates

/**
 * @author Grubber
 */
class DTApplication : MultiDexApplication() {
    companion object {
        fun from(context: Context): DTApplication {
            return context.applicationContext as DTApplication
        }
    }

    var applicationComponent by Delegates.notNull<ApplicationComponent>()
    var articleRepositoryComponent by Delegates.notNull<ArticleRepositoryComponent>()
    var bookRepositoryComponent by Delegates.notNull<BookRepositoryComponent>()

    @Inject
    lateinit var databaseManager: DatabaseManager
    @Inject
    lateinit var picasso: Picasso
    @Inject
    lateinit var toastHelper: ToastHelper
    @Inject
    lateinit var deviceUtils: DeviceUtils

    override fun onCreate() {
        super.onCreate()

        _setupObjectGraph()
        _setupAnalytics()

        databaseManager.open()
    }

    private fun _setupObjectGraph() {
        applicationComponent = DaggerApplicationComponent.builder()
                .androidModule(AndroidModule(this))
                .dataModule(DataModule())
                .utilsModule(UtilsModule())
                .build()

        articleRepositoryComponent = applicationComponent.plus(ArticleRepositoryModule())
        bookRepositoryComponent = applicationComponent.plus(BookRepositoryModule())

        applicationComponent.inject(this)
    }

    private fun _setupAnalytics() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
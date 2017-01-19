package com.github.xtorrent.dailytopic.book

import dagger.Module
import dagger.Provides

/**
 * Created by grubber on 2017/1/18.
 */
@Module
class BookPresenterModule(val view: BookContract.View) {
    @Provides
    fun provideBookContractView(): BookContract.View {
        return view
    }
}
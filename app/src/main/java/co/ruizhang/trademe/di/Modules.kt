package co.ruizhang.trademe.di

import co.ruizhang.trademe.data.*
import co.ruizhang.trademe.viewmodels.CategoryViewModel
import co.ruizhang.trademe.viewmodels.ListedItemDetailViewModel
import co.ruizhang.trademe.viewmodels.SearchListViewModel
import okhttp3.OkHttpClient
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    single<CategoryRepository> { CategoryRepositoryImpl(get()) }
    single<SearchRepository> { SearchRepositoryImpl(get()) }
    single<ListedItemDetailRepository> { ListedItemDetailRepositoryImpl(get()) }
    viewModel { CategoryViewModel(get()) }
    viewModel { SearchListViewModel(get()) }
    viewModel { ListedItemDetailViewModel(get()) }
}


val networkModule = module {
    single { provideDefaultOkhttpClient() }
    single { provideRetrofit(get()) }
    single { provideTrademeApi(get()) }
}

fun provideDefaultOkhttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
        .build()
}

fun provideRetrofit(client: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl(SERVER_BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
}

fun provideTrademeApi(retrofit: Retrofit): TrademeApi = retrofit.create(TrademeApi::class.java)

const val SERVER_BASE_URL = "https://api.tmsandbox.co.nz"
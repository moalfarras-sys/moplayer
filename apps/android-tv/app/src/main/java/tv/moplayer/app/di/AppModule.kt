package tv.moplayer.app.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import tv.moplayer.core.database.MoPlayerDatabase
import tv.moplayer.data.local.LocalStorageRepository
import tv.moplayer.data.repository.BasicEpgProvider
import tv.moplayer.data.repository.InMemoryPlaylistIngestor
import tv.moplayer.data.repository.RuleBasedRecommendationEngine
import tv.moplayer.data.widgets.WidgetDataRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MoPlayerDatabase =
        Room.databaseBuilder(context, MoPlayerDatabase::class.java, "moplayer.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideLocalStorageRepository(db: MoPlayerDatabase): LocalStorageRepository = LocalStorageRepository(db)

    @Provides
    @Singleton
    fun providePlaylistIngestor(): InMemoryPlaylistIngestor = InMemoryPlaylistIngestor()

    @Provides
    @Singleton
    fun provideEpgProvider(): BasicEpgProvider = BasicEpgProvider()

    @Provides
    @Singleton
    fun provideRecommendationEngine(): RuleBasedRecommendationEngine = RuleBasedRecommendationEngine()

    @Provides
    @Singleton
    fun provideWidgetDataRepository(): WidgetDataRepository = WidgetDataRepository()
}
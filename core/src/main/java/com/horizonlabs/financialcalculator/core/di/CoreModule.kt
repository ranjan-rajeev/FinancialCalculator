package com.horizonlabs.financialcalculator.core.di

import com.horizonlabs.financialcalculator.core.data.db.AppDatabase
import com.horizonlabs.financialcalculator.core.data.remote.FinanceCalculatorService
import com.horizonlabs.financialcalculator.core.data.repository.CalculatorRepositoryImpl
import com.horizonlabs.financialcalculator.core.data.repository.ConfigRepositoryImpl
import com.horizonlabs.financialcalculator.core.data.repository.HistoryRepositoryImpl
import com.horizonlabs.financialcalculator.core.domain.engine.CalculatorEngine
import com.horizonlabs.financialcalculator.core.domain.engine.EmiCalculatorEngine
import com.horizonlabs.financialcalculator.core.domain.engine.FdCalculatorEngine
import com.horizonlabs.financialcalculator.core.domain.engine.FlatVsReducingEngine
import com.horizonlabs.financialcalculator.core.domain.engine.GenericFormulaEngine
import com.horizonlabs.financialcalculator.core.domain.engine.GstCalculatorEngine
import com.horizonlabs.financialcalculator.core.domain.engine.PpfCalculatorEngine
import com.horizonlabs.financialcalculator.core.domain.engine.RdCalculatorEngine
import com.horizonlabs.financialcalculator.core.domain.engine.SipCalculatorEngine
import com.horizonlabs.financialcalculator.core.domain.engine.VatCalculatorEngine
import com.horizonlabs.financialcalculator.core.domain.engine.CompareLoanEngine
import com.horizonlabs.financialcalculator.core.domain.repository.CalculatorRepository
import com.horizonlabs.financialcalculator.core.domain.repository.ConfigRepository
import com.horizonlabs.financialcalculator.core.domain.repository.HistoryRepository
import com.horizonlabs.financialcalculator.core.domain.usecase.CalculateUseCase
import com.horizonlabs.financialcalculator.core.domain.usecase.CalculateUseCaseImpl
import com.horizonlabs.financialcalculator.core.domain.usecase.GetAppConfigUseCase
import com.horizonlabs.financialcalculator.core.domain.usecase.GetAppConfigUseCaseImpl
import com.horizonlabs.financialcalculator.core.domain.usecase.GetCalculatorConfigUseCase
import com.horizonlabs.financialcalculator.core.domain.usecase.GetCalculatorConfigUseCaseImpl
import com.horizonlabs.financialcalculator.core.domain.usecase.GetDashboardUseCase
import com.horizonlabs.financialcalculator.core.domain.usecase.GetDashboardUseCaseImpl
import com.horizonlabs.financialcalculator.core.domain.usecase.GetHistoryUseCase
import com.horizonlabs.financialcalculator.core.domain.usecase.GetHistoryUseCaseImpl
import com.horizonlabs.financialcalculator.core.domain.usecase.SaveHistoryUseCase
import com.horizonlabs.financialcalculator.core.domain.usecase.SaveHistoryUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import com.horizonlabs.financialcalculator.core.data.remote.KotlinxConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {

    @Provides
    @Singleton
    fun provideDatabase(@dagger.hilt.android.qualifiers.ApplicationContext context: android.content.Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideNetworkJson(): Json = Json {
        ignoreUnknownKeys = true
        classDiscriminator = "type"
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, networkJson: Json): Retrofit {
        return Retrofit.Builder()
            .baseUrl(com.horizonlabs.financialcalculator.core.util.Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(KotlinxConverterFactory(networkJson))
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): FinanceCalculatorService {
        return retrofit.create(FinanceCalculatorService::class.java)
    }

    @Provides
    @Singleton
    fun provideCalculatorRepository(
        database: AppDatabase,
        apiService: FinanceCalculatorService
    ): CalculatorRepository {
        return CalculatorRepositoryImpl(database, apiService)
    }

    @Provides
    @Singleton
    fun provideHistoryRepository(database: AppDatabase): HistoryRepository {
        return HistoryRepositoryImpl(database)
    }

    @Provides
    @Singleton
    fun provideConfigRepository(
        database: AppDatabase,
        apiService: FinanceCalculatorService
    ): ConfigRepository {
        return ConfigRepositoryImpl(database, apiService)
    }

    @Provides
    @Singleton
    fun provideGetDashboardUseCase(repository: CalculatorRepository): GetDashboardUseCase {
        return GetDashboardUseCaseImpl(repository)
    }

    @Provides
    @Singleton
    fun provideGetCalculatorConfigUseCase(repository: CalculatorRepository): GetCalculatorConfigUseCase {
        return GetCalculatorConfigUseCaseImpl(repository)
    }

    @Provides
    @Singleton
    fun provideCalculateUseCase(
        emiEngine: EmiCalculatorEngine,
        fdEngine: FdCalculatorEngine,
        sipEngine: SipCalculatorEngine,
        rdEngine: RdCalculatorEngine,
        ppfEngine: PpfCalculatorEngine,
        gstEngine: GstCalculatorEngine,
        vatEngine: VatCalculatorEngine,
        genericEngine: GenericFormulaEngine,
        compareLoanEngine: CompareLoanEngine,
        flatVsReducingEngine: FlatVsReducingEngine
    ): CalculateUseCase {
        val engines = mapOf<String, CalculatorEngine>(
            com.horizonlabs.financialcalculator.core.util.Constants.EngineType.EMI_CALCULATOR to emiEngine,
            com.horizonlabs.financialcalculator.core.util.Constants.EngineType.FD_CALCULATOR to fdEngine,
            com.horizonlabs.financialcalculator.core.util.Constants.EngineType.SIP_CALCULATOR to sipEngine,
            com.horizonlabs.financialcalculator.core.util.Constants.EngineType.RD_CALCULATOR to rdEngine,
            com.horizonlabs.financialcalculator.core.util.Constants.EngineType.PPF_CALCULATOR to ppfEngine,
            com.horizonlabs.financialcalculator.core.util.Constants.EngineType.GST_CALCULATOR to gstEngine,
            com.horizonlabs.financialcalculator.core.util.Constants.EngineType.VAT_CALCULATOR to vatEngine,
            com.horizonlabs.financialcalculator.core.util.Constants.EngineType.GENERIC_FORMULA to genericEngine,
            com.horizonlabs.financialcalculator.core.util.Constants.EngineType.COMPARE_LOAN to compareLoanEngine,
            com.horizonlabs.financialcalculator.core.util.Constants.EngineType.FLAT_VS_REDUCING to flatVsReducingEngine
        )
        return CalculateUseCaseImpl(engines)
    }

    @Provides
    @Singleton
    fun provideSaveHistoryUseCase(repository: HistoryRepository): SaveHistoryUseCase {
        return SaveHistoryUseCaseImpl(repository)
    }

    @Provides
    @Singleton
    fun provideGetHistoryUseCase(repository: HistoryRepository): GetHistoryUseCase {
        return GetHistoryUseCaseImpl(repository)
    }

    @Provides
    @Singleton
    fun provideGetAppConfigUseCase(repository: ConfigRepository): GetAppConfigUseCase {
        return GetAppConfigUseCaseImpl(repository)
    }

    // Engine implementations
    @Provides
    @Singleton
    fun provideEmiCalculatorEngine(): EmiCalculatorEngine = EmiCalculatorEngine()

    @Provides
    @Singleton
    fun provideFdCalculatorEngine(): FdCalculatorEngine = FdCalculatorEngine()

    @Provides
    @Singleton
    fun provideSipCalculatorEngine(): SipCalculatorEngine = SipCalculatorEngine()

    @Provides
    @Singleton
    fun provideRdCalculatorEngine(): RdCalculatorEngine = RdCalculatorEngine()

    @Provides
    @Singleton
    fun providePpfCalculatorEngine(): PpfCalculatorEngine = PpfCalculatorEngine()

    @Provides
    @Singleton
    fun provideGstCalculatorEngine(): GstCalculatorEngine = GstCalculatorEngine()

    @Provides
    @Singleton
    fun provideVatCalculatorEngine(): VatCalculatorEngine = VatCalculatorEngine()

    @Provides
    @Singleton
    fun provideGenericFormulaEngine(): GenericFormulaEngine = GenericFormulaEngine()

    @Provides
    @Singleton
    fun provideCompareLoanEngine(): CompareLoanEngine = CompareLoanEngine()

    @Provides
    @Singleton
    fun provideFlatVsReducingEngine(): FlatVsReducingEngine = FlatVsReducingEngine()
}
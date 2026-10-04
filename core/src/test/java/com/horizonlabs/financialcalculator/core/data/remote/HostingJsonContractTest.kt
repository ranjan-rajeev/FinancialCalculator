package com.horizonlabs.financialcalculator.core.data.remote

import com.horizonlabs.financialcalculator.core.domain.model.CalculatorConfig
import com.horizonlabs.financialcalculator.core.domain.model.CalculatorInputValues
import com.horizonlabs.financialcalculator.core.domain.model.DashboardResponse
import com.horizonlabs.financialcalculator.core.domain.model.InputDataType
import com.horizonlabs.financialcalculator.core.domain.model.InputFieldConfig
import com.horizonlabs.financialcalculator.core.domain.engine.CalculatorEngine
import com.horizonlabs.financialcalculator.core.domain.engine.CompareLoanEngine
import com.horizonlabs.financialcalculator.core.domain.engine.EmiCalculatorEngine
import com.horizonlabs.financialcalculator.core.domain.engine.FdCalculatorEngine
import com.horizonlabs.financialcalculator.core.domain.engine.FlatVsReducingEngine
import com.horizonlabs.financialcalculator.core.domain.engine.GenericFormulaEngine
import com.horizonlabs.financialcalculator.core.domain.engine.GstCalculatorEngine
import com.horizonlabs.financialcalculator.core.domain.engine.PpfCalculatorEngine
import com.horizonlabs.financialcalculator.core.domain.engine.RdCalculatorEngine
import com.horizonlabs.financialcalculator.core.domain.engine.SipCalculatorEngine
import com.horizonlabs.financialcalculator.core.domain.engine.VatCalculatorEngine
import com.horizonlabs.financialcalculator.core.util.Constants
import com.horizonlabs.financialcalculator.core.util.Result
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Guards the JSON contract between `tools/generate_hosting_json.py` and the app.
 *
 * The generated tree under `github-pages/json/v2` is what the APK fetches at runtime,
 * so a mismatch here shows up as an empty dashboard rather than a compile error.
 */
class HostingJsonContractTest {

    private val json = Json {
        ignoreUnknownKeys = true
        classDiscriminator = "type"
    }

    private val v2Dir = File("../github-pages/json/v2")

    private fun read(name: String): String {
        val file = File(v2Dir, name)
        assertTrue("Missing generated file: ${file.path}", file.exists())
        return file.readText()
    }

    @Test
    fun `config decodes`() {
        val config = json.decodeFromString(
            com.horizonlabs.financialcalculator.core.domain.model.AppConfig.serializer(),
            read("config.json")
        )
        assertEquals(2, config.inAppReviewCooldownDays)
    }

    @Test
    fun `dashboard decodes`() {
        val dashboard = json.decodeFromString(DashboardResponse.serializer(), read("dashboard.json"))
        assertTrue("Dashboard has no components", dashboard.components.isNotEmpty())
        assertEquals(
            "Components must be emitted in ascending position order",
            dashboard.components.sortedBy { it.position },
            dashboard.components
        )
    }

    @Test
    fun `every calculator decodes`() {
        val files = File(v2Dir, "calculators").listFiles()?.filter { it.extension == "json" }
        assertTrue("No calculator files generated", !files.isNullOrEmpty())

        files!!.sortedBy { it.name }.forEach { file ->
            val config = json.decodeFromString(CalculatorConfig.serializer(), file.readText())

            assertTrue("${file.name}: empty id", config.id.isNotBlank())
            assertTrue("${file.name}: empty name", config.name.isNotBlank())
            assertTrue("${file.name}: no engine", config.engine.isNotBlank())
            assertTrue("${file.name}: no input fields", config.inputFields.isNotEmpty())
            assertTrue("${file.name}: no summary", config.outputConfig.summary.isNotEmpty())

            // Input keys must be unique: the engine reads them from a single map.
            val keys = config.inputFields.map { it.key }
            assertEquals("${file.name}: duplicate input keys", keys.size, keys.distinct().size)

            // Every field needs a stable order so the UI renders predictably.
            val orders = config.inputFields.map { it.order }
            assertEquals("${file.name}: duplicate field order", orders.size, orders.distinct().size)

            // Summary keys are what the UI looks up in the engine result.
            val summaryKeys = config.outputConfig.summary.map { it.key }
            assertEquals(
                "${file.name}: duplicate summary keys",
                summaryKeys.size,
                summaryKeys.distinct().size
            )
        }
    }

    @Test
    fun `dashboard calculator ids resolve to a hosted calculator file`() {
        val dashboard = json.decodeFromString(DashboardResponse.serializer(), read("dashboard.json"))

        dashboard.components
            .filterIsInstance<com.horizonlabs.financialcalculator.core.domain.model.DashboardComponent.CalculatorGrid>()
            .flatMap { it.data.calculators }
            .forEach { card ->
                assertTrue(
                    "Dashboard references ${card.id} but no v2/calculators/${card.id}.json exists",
                    File(v2Dir, "calculators/${card.id}.json").exists()
                )
            }
    }

    /**
     * A chart names its series through `dataKeys`, which are looked up in the
     * engine's `rawValues`. Nothing else connects the two, so a typo or an engine
     * that forgot to publish a figure renders as a silently absent chart. Running
     * the engine is what makes the failure visible at build time.
     */
    @Test
    fun `chart data keys are published by the engine`() {
        engines().forEach { (engineName, engine) ->
            File(v2Dir, "calculators").listFiles()
                ?.filter { it.extension == "json" }
                ?.sortedBy { it.name }
                ?.forEach { file ->
                    val config = json.decodeFromString(CalculatorConfig.serializer(), file.readText())
                    if (config.engine != engineName) return@forEach

                    val charts = config.outputConfig.charts
                    if (charts.isEmpty()) return@forEach

                    val result = engine.calculate(
                        CalculatorInputValues(sampleInputs(config))
                    )
                    assertTrue(
                        "${file.name}: ${engineName} rejected the generated config's own fields: $result",
                        result is Result.Success
                    )
                    val raw = (result as Result.Success).data.rawValues

                    charts.forEach { chart ->
                        assertTrue(
                            "${file.name}: chart '${chart.title}' needs at least two series to be readable",
                            chart.dataKeys.size >= 2
                        )
                        assertEquals(
                            "${file.name}: chart '${chart.title}' repeats a data key",
                            chart.dataKeys.size,
                            chart.dataKeys.distinct().size
                        )
                        chart.dataKeys.forEach { key ->
                            assertTrue(
                                "${file.name}: chart '${chart.title}' references '$key' but " +
                                    "${engineName} publishes ${raw.keys.sorted()}",
                                raw.containsKey(key)
                            )
                        }
                    }
                }
        }
    }

    /** Engines are plain classes, so the contract test builds them without Hilt. */
    private fun engines(): Map<String, CalculatorEngine> = mapOf(
        Constants.EngineType.EMI_CALCULATOR to EmiCalculatorEngine(),
        Constants.EngineType.FD_CALCULATOR to FdCalculatorEngine(),
        Constants.EngineType.SIP_CALCULATOR to SipCalculatorEngine(),
        Constants.EngineType.RD_CALCULATOR to RdCalculatorEngine(),
        Constants.EngineType.PPF_CALCULATOR to PpfCalculatorEngine(),
        Constants.EngineType.GST_CALCULATOR to GstCalculatorEngine(),
        Constants.EngineType.VAT_CALCULATOR to VatCalculatorEngine(),
        Constants.EngineType.GENERIC_FORMULA to GenericFormulaEngine(),
        Constants.EngineType.COMPARE_LOAN to CompareLoanEngine(),
        Constants.EngineType.FLAT_VS_REDUCING to FlatVsReducingEngine()
    )

    /**
     * Plausible values for every field the config declares, so the engine gets a
     * complete input map. A required numeric field takes its published minimum
     * where there is one, otherwise 1, which keeps every branch reachable.
     */
    private fun sampleInputs(config: CalculatorConfig): Map<String, Any> = buildMap {
        config.inputFields.forEach { field ->
            when (field) {
                is InputFieldConfig.EditText -> {
                    val numeric = field.inputType == InputDataType.NUMBER ||
                        field.inputType == InputDataType.DECIMAL
                    // A published default is what a real user sees first, so it wins
                    // over the validation minimum. FD's year/month/day default to
                    // 5/0/0 and fall back to 0/0/0, which the engine rejects as an
                    // empty tenure.
                    val fallback = field.validation?.min ?: 1.0
                    val value = field.defaultValue ?: if (numeric) fallback.toString() else "1"
                    put(field.key, value)
                }

                is InputFieldConfig.Spinner ->
                    put(field.key, field.defaultValue ?: field.options.firstOrNull()?.value ?: "")

                is InputFieldConfig.DatePicker ->
                    put(field.key, field.defaultValue)

                is InputFieldConfig.Button,
                is InputFieldConfig.Spacer,
                is InputFieldConfig.InfoText -> Unit
            }
        }
        // Engines that dispatch on calculator id need it, exactly as the view model
        // injects it at runtime.
        put(CommonCalculatorIds.CALCULATOR_ID, config.id)
    }
}

private object CommonCalculatorIds {
    const val CALCULATOR_ID = "calculatorId"
}

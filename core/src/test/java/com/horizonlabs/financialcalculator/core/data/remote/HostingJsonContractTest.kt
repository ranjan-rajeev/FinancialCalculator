package com.horizonlabs.financialcalculator.core.data.remote

import com.horizonlabs.financialcalculator.core.domain.model.CalculatorConfig
import com.horizonlabs.financialcalculator.core.domain.model.DashboardResponse
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
}

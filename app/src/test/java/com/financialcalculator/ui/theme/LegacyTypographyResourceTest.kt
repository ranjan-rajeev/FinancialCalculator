package com.financialcalculator.ui.theme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Guards the type tokens against `res/values/styles.xml` and locks the
 * line-height decision that MIGRATION_PLAN.md calls out as a parity trap.
 */
class LegacyTypographyResourceTest {

    @Test
    fun textViewStyleVariantsKeepTheirSizeAndColour() {
        val styles = parseStyles()

        assertStyle(styles, "TextViewStyle.Large", "15sp", "header_dark_text")
        assertStyle(styles, "TextViewStyle.Medium", "13sp", "header_light_text")
        assertStyle(styles, "TextViewStyle.Small", "12sp", "description_text")
        assertStyle(styles, "TextViewStyle.VerySmall", "10sp", "description_text")
    }

    @Test
    fun defaultTextViewStyleInheritsSecondaryTextColour() {
        val styles = parseStyles()
        val base = styles.getValue("TextViewStyle")
        assertEquals("@color/secondary_text_color", base["android:textColor"])
        // The base style also pins these three; they affect every label.
        assertEquals("@android:color/transparent", base["android:background"])
        assertEquals("false", base["android:textAllCaps"])
        assertEquals(LegacyColors.SecondaryText, LegacyTextStyles.Default.color)
    }

    @Test
    fun typographyStylesMirrorTheXmlSizes() {
        assertEquals(15f, LegacyTextStyles.Large.fontSize.value, 0.001f)
        assertEquals(13f, LegacyTextStyles.Medium.fontSize.value, 0.001f)
        assertEquals(12f, LegacyTextStyles.Small.fontSize.value, 0.001f)
        assertEquals(10f, LegacyTextStyles.VerySmall.fontSize.value, 0.001f)

        assertEquals(LegacyColors.HeaderDarkText, LegacyTextStyles.Large.color)
        assertEquals(LegacyColors.HeaderLightText, LegacyTextStyles.Medium.color)
        assertEquals(LegacyColors.DescriptionText, LegacyTextStyles.Small.color)
        assertEquals(LegacyColors.DescriptionText, LegacyTextStyles.VerySmall.color)
    }

    @Test
    fun heroFigureMatchesThe25spBoldPrimaryDarkOutputValue() {
        // `item_output_key_value.xml` declares textSize 25sp, bold, colorPrimaryDark.
        assertEquals(25f, LegacyTextStyles.OutputValue.fontSize.value, 0.001f)
        assertEquals(LegacyColors.ColorPrimaryDark, LegacyTextStyles.OutputValue.color)
        assertEquals(
            androidx.compose.ui.text.font.FontWeight.Bold,
            LegacyTextStyles.OutputValue.fontWeight,
        )
    }

    @Test
    fun sectionHeaderMatchesItemHeaderOverride() {
        // `item_header.xml` uses TextViewStyle.Large but overrides the colour to
        // colorPrimaryDark and adds bold.
        assertEquals(15f, LegacyTextStyles.SectionHeader.fontSize.value, 0.001f)
        assertEquals(LegacyColors.ColorPrimaryDark, LegacyTextStyles.SectionHeader.color)
        assertEquals(
            androidx.compose.ui.text.font.FontWeight.Bold,
            LegacyTextStyles.SectionHeader.fontWeight,
        )
    }

    @Test
    fun dashboardCellLabelIs10spHeaderLightText() {
        // `item_dashboard.xml` uses TextViewStyle.VerySmall, overrides the colour
        // to header_light_text, and sits 10dp below the 50dp icon.
        assertEquals(10f, LegacyTextStyles.DashboardCellLabel.fontSize.value, 0.001f)
        assertEquals(LegacyColors.HeaderLightText, LegacyTextStyles.DashboardCellLabel.color)
    }

    @Test
    fun everyStyleLeavesLineHeightUnspecified() {
        // This is the parity trap. Material3's stock Typography hard-codes a
        // lineHeight per style (24sp for a 16sp body, and so on); a legacy
        // TextView measures its line box from the font metrics instead. Pinning
        // any explicit lineHeight here would silently change every migrated
        // label's height, so assert that nothing has crept in.
        val styles = listOf(
            LegacyTypography.displayLarge,
            LegacyTypography.displayMedium,
            LegacyTypography.titleLarge,
            LegacyTypography.titleMedium,
            LegacyTypography.bodyMedium,
            LegacyTypography.labelSmall,
            LegacyTextStyles.Default,
            LegacyTextStyles.OutputValue,
            LegacyTextStyles.RingPercent,
            LegacyTextStyles.RingAmount,
            LegacyTextStyles.OutputLabel,
            LegacyTextStyles.SectionHeader,
            LegacyTextStyles.DashboardCellLabel,
            LegacyTextStyles.HistoryPrincipal,
            LegacyTextStyles.HistoryTerm,
            LegacyTextStyles.HistoryDate,
        )
        for (style in styles) {
            assertTrue(
                "lineHeight must stay TextUnit.Unspecified for platform parity",
                style.lineHeight == androidx.compose.ui.unit.TextUnit.Unspecified,
            )
            assertEquals(
                "lineHeightStyle must not trim, or wrap_content heights shrink",
                androidx.compose.ui.text.style.LineHeightStyle.Trim.None,
                style.lineHeightStyle?.trim,
            )
        }
    }

    @Test
    fun documentedLineHeightConstantMatchesRobotoWinMetrics() {
        // ascent 1946 + descent 512 = 2458 units of a 2048-unit em.
        assertEquals(2458f / 2048f, LegacyLineHeight, 1e-6f)
        assertEquals(1.2001953f, LegacyLineHeight, 1e-6f)
    }

    private fun assertStyle(
        styles: Map<String, Map<String, String>>,
        name: String,
        expectedSize: String,
        expectedColorRes: String,
    ) {
        val style = styles[name] ?: error("style $name not found in styles.xml")
        assertEquals("$name textSize", expectedSize, style["android:textSize"])
        assertEquals("$name textColor", "@color/$expectedColorRes", style["android:textColor"])
    }

    /**
     * `styles.xml` expresses values as `<item name="...">value</item>` children,
     * not as attributes on the `<style>` element, so the style element is
     * flattened into a name-to-value map before anything is asserted.
     */
    private fun parseStyles(): Map<String, Map<String, String>> {
        val file = locateResourceFile("values/styles.xml")
        val document = DocumentBuilderFactory.newInstance()
            .apply { isNamespaceAware = false }
            .newDocumentBuilder()
            .parse(file)
        val nodes = document.getElementsByTagName("style")
        val result = HashMap<String, Map<String, String>>(nodes.length)
        for (i in 0 until nodes.length) {
            val element = nodes.item(i) as Element
            val items = HashMap<String, String>()
            val children = element.getElementsByTagName("item")
            for (j in 0 until children.length) {
                val child = children.item(j) as Element
                items[child.getAttribute("name")] = child.textContent.trim()
            }
            result[element.getAttribute("name")] = items
        }
        return result
    }

    private fun locateResourceFile(relativePath: String): File {
        val candidates = listOf(
            File("src/main/res/$relativePath"),
            File("app/src/main/res/$relativePath"),
        )
        return candidates.firstOrNull { it.isFile }
            ?: error("Could not locate src/main/res/$relativePath from ${File(".").absolutePath}")
    }
}
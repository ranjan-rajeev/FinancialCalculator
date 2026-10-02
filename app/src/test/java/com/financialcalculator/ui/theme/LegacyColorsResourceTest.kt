package com.financialcalculator.ui.theme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Guards the design tokens against the XML they were transcribed from.
 *
 * `colors.xml` will keep living next to the Kotlin for as long as the Java
 * screens remain, and nothing stops someone from editing one without the other.
 * These tests parse the resource files straight off disk and fail the moment a
 * token and its `<color>` entry disagree, which is the drift that would
 * otherwise only surface as a visual regression during manual QA.
 */
class LegacyColorsResourceTest {

    private val colours: Map<String, String> by lazy { parseColorsXml() }

    @Test
    fun everyTokenMatchesItsColorResource() {
        val expected = mapOf(
            "ColorPrimary" to "colorPrimary",
            "ColorPrimaryDark" to "colorPrimaryDark",
            "ColorAccent" to "colorAccent",
            "BgLight" to "bg_light",
            "DashboardBg" to "dashboard_bg",
            "HeaderDarkText" to "header_dark_text",
            "HeaderLightText" to "header_light_text",
            "DescriptionText" to "description_text",
            "SecondaryText" to "secondary_text_color",
            "ApplicationPrimaryText" to "application_primary_text_color",
            "ApplicationSecondaryText" to "application_secondary_text_color",
            "Black" to "uvv_black",
            "White" to "white",
            "Divider" to "divider",
            "GreyD8" to "colorGreyD8",
            "GreyEA" to "colorGreyEA",
            "LightGrey" to "lightGrey",
            "FieldFill" to "offer_info_white",
            "FieldBorder" to "bg",
            "Pumpkin" to "colorPumpkin",
            "GreenDescent" to "green_descent",
            "RedDescent" to "red_descent",
            "BlueDescent" to "blue_descent",
            "ProgressBlue" to "progress_blue",
        )

        val actual = mapOf(
            "ColorPrimary" to LegacyColors.ColorPrimary,
            "ColorPrimaryDark" to LegacyColors.ColorPrimaryDark,
            "ColorAccent" to LegacyColors.ColorAccent,
            "BgLight" to LegacyColors.BgLight,
            "DashboardBg" to LegacyColors.DashboardBg,
            "HeaderDarkText" to LegacyColors.HeaderDarkText,
            "HeaderLightText" to LegacyColors.HeaderLightText,
            "DescriptionText" to LegacyColors.DescriptionText,
            "SecondaryText" to LegacyColors.SecondaryText,
            "ApplicationPrimaryText" to LegacyColors.ApplicationPrimaryText,
            "ApplicationSecondaryText" to LegacyColors.ApplicationSecondaryText,
            "Black" to LegacyColors.Black,
            "White" to LegacyColors.White,
            "Divider" to LegacyColors.Divider,
            "GreyD8" to LegacyColors.GreyD8,
            "GreyEA" to LegacyColors.GreyEA,
            "LightGrey" to LegacyColors.LightGrey,
            "FieldFill" to LegacyColors.FieldFill,
            "FieldBorder" to LegacyColors.FieldBorder,
            "Pumpkin" to LegacyColors.Pumpkin,
            "GreenDescent" to LegacyColors.GreenDescent,
            "RedDescent" to LegacyColors.RedDescent,
            "BlueDescent" to LegacyColors.BlueDescent,
            "ProgressBlue" to LegacyColors.ProgressBlue,
        )

        assertEquals("token list and resource list have drifted apart", expected.keys, actual.keys)

        for ((token, resourceName) in expected) {
            val xmlValue = colours[resourceName]
                ?: error("@color/$resourceName is not declared in colors.xml")
            assertEquals(
                "LegacyColors.$token no longer matches @color/$resourceName",
                normaliseAndroidHex(xmlValue),
                toArgbHex(actual.getValue(token)),
            )
        }
    }

    @Test
    fun colorAccentStillResolvesThroughColorCta() {
        assertEquals(
            "colorAccent is an alias for colorCTA; both must stay #FF1744",
            normaliseAndroidHex(colours.getValue("colorCTA")),
            toArgbHex(LegacyColors.ColorAccent),
        )
    }

    @Test
    fun fieldFillKeepsItsEightySevenPercentAlpha() {
        // `#DDFFFFFF` composites the field over whatever is behind it. Losing the
        // alpha would make the field opaque white and flatten the card behind it.
        assertEquals("#DDFFFFFF", toArgbHex(LegacyColors.FieldFill))
        assertEquals(221 / 255f, LegacyColors.FieldFill.alpha, 0.002f)
        assertEquals(1f, LegacyColors.White.alpha, 0.001f)
    }

    @Test
    fun carouselDotColoursArePresentInTheDrawableNotColorsXml() {
        // These two are hard-coded in `rounded_corner_*_grey.xml`, so there is no
        // resource to cross-check; assert the literal so an edit is deliberate.
        assertEquals("#FFC5C5C5", toArgbHex(LegacyHardCodedColors.CarouselDotUnselected))
        assertEquals("#FF949494", toArgbHex(LegacyHardCodedColors.CarouselDotSelected))
    }

    @Test
    fun colourSchemeDoesNotLeakMaterialDefaults() {
        val scheme = LegacyColorScheme
        // Any role the legacy XML never defined must have been filled from an
        // existing `colors.xml` entry, not from the Material3 default palette.
        assertEquals(LegacyColors.RedDescent, scheme.error)
        assertEquals(LegacyColors.FieldBorder, scheme.outline)
        assertTrue(
            "surfaceTint must be transparent or M3 will tint the cards",
            scheme.surfaceTint.alpha == 0f,
        )
    }

    private fun toArgbHex(colour: androidx.compose.ui.graphics.Color): String =
        String.format(
            "#%02X%02X%02X%02X",
            (colour.alpha * 255.0f).toInt(),
            (colour.red * 255.0f).toInt(),
            (colour.green * 255.0f).toInt(),
            (colour.blue * 255.0f).toInt(),
        )

    /**
     * Expands any of the four `#` hex forms Android accepts into `#AARRGGBB`, so
     * the six-digit entries that make up most of `colors.xml` can be compared
     * against the eight-digit form Compose produces.
     *
     * Android puts the two leading digits in `#AARRGGBB` but `#RRGGBB` when the
     * value is fully opaque, and `#ARGB` only as a shorthand.
     */
    private fun normaliseAndroidHex(raw: String): String {
        val hex = raw.trim().removePrefix("#")
        val expanded = when (hex.length) {
            3 -> "FF" + hex.map { "$it$it" }.joinToString("")
            4 -> hex.map { "$it$it" }.joinToString("")
            6 -> "FF$hex"
            8 -> hex
            else -> error("Unexpected colour literal #$hex in colors.xml")
        }
        return "#$expanded".uppercase()
    }

    private fun parseColorsXml(): Map<String, String> {
        val file = locateResourceFile("values/colors.xml")
        val document = DocumentBuilderFactory.newInstance()
            .apply { isNamespaceAware = false }
            .newDocumentBuilder()
            .parse(file)
        val nodes = document.getElementsByTagName("color")

        val raw = HashMap<String, String>(nodes.length)
        for (i in 0 until nodes.length) {
            val element = nodes.item(i)
            val name = element.attributes.getNamedItem("name")?.nodeValue ?: continue
            raw[name] = element.textContent.trim()
        }

        // Several entries are aliases rather than literals — `colorAccent` is
        // `@color/colorCTA`, `colorGrey21` is `@color/colorPrimary`. Follow them
        // so the test sees the colour the app actually resolves.
        val resolved = HashMap<String, String>(raw.size)
        for ((name, value) in raw) {
            var current = value
            var hops = 0
            while (current.startsWith("@color/") && hops < MAX_ALIAS_HOPS) {
                current = raw[current.removePrefix("@color/")]
                    ?: error("@color/${value.removePrefix("@color/")} is referenced but not declared")
                hops++
            }
            resolved[name] = current
        }
        return resolved
    }

    companion object {
        /** Guards against an accidental alias cycle in `colors.xml`. */
        private const val MAX_ALIAS_HOPS = 8

        /**
         * Locates a resource file relative to whichever directory the test JVM
         * was started in — Gradle uses the module directory for Android unit
         * tests, but IDE runners differ.
         */
        fun locateResourceFile(relativePath: String): File {
            val candidates = listOf(
                File("src/main/res/$relativePath"),
                File("app/src/main/res/$relativePath"),
            )
            return candidates.firstOrNull { it.isFile }
                ?: error(
                    "Could not locate src/main/res/$relativePath from ${File(".").absolutePath}",
                )
        }
    }
}
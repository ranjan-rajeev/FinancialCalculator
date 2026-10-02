package com.financialcalculator.ui.components

import com.financialcalculator.ui.theme.LegacyColors
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Element

/**
 * Locks the component geometry against the drawables that define it.
 *
 * These are the numbers that cannot be inferred from a screenshot and that a
 * well-meaning "let's round this to 16dp" would quietly break: the 10dp notch
 * inset in `corner_shape`, the 8dp shape padding, the 15dp button radius, the
 * 2.3 inner-radius ratio on the ring drawables.
 */
class LegacyComponentGeometryTest {

    @Test
    fun fieldMetricsMatchCornerShapeDrawable() {
        val doc = parseDrawable("corner_shape.xml")
        // `<item android:top="10dp">` — the notch that the floating hint sits in.
        val inset = doc.getElementsByTagName("item")
        val topInset = (0 until inset.length)
            .map { inset.item(it) as Element }
            .first { it.getAttribute("android:top").isNotEmpty() }
            .getAttribute("android:top")
        assertEquals("10dp", topInset)

        // The shape shared by the focused and unfocused branches.
        val shape = doc.getElementsByTagName("shape").item(1) as Element
        val corners = shape.getElementsByTagName("corners").item(0) as Element
        assertEquals("5dp", corners.getAttribute("android:radius"))

        val stroke = shape.getElementsByTagName("stroke").item(0) as Element
        assertEquals("1dp", stroke.getAttribute("android:width"))
        assertEquals("@color/bg", stroke.getAttribute("android:color"))

        val padding = shape.getElementsByTagName("padding").item(0) as Element
        assertEquals("8dp", padding.getAttribute("android:left"))
        assertEquals("8dp", padding.getAttribute("android:right"))
        assertEquals("8dp", padding.getAttribute("android:top"))

        // Kotlin side must agree with the drawable.
        assertEquals(10f, LegacyFieldMetrics.NotchHeight.value, 0.001f)
        assertEquals(40f, LegacyFieldMetrics.BoxHeight.value, 0.001f)
        assertEquals(50f, LegacyFieldMetrics.FieldHeight.value, 0.001f)
        assertEquals(1f, LegacyFieldMetrics.BorderWidth.value, 0.001f)
        assertEquals(8f, LegacyFieldMetrics.InnerPadding.value, 0.001f)
        assertEquals(5f, LegacyFieldMetrics.CornerRadius.value, 0.001f)
    }

    @Test
    fun focusedFieldUsesPrimaryBorder() {
        // The focused branch of `corner_shape` strokes with colorPrimary, not the
        // `bg` grey used at rest.
        val doc = parseDrawable("corner_shape.xml")
        val strokes = doc.getElementsByTagName("stroke")
        assertEquals(2, strokes.length)
        val focusedStroke = strokes.item(0) as Element
        assertEquals("@color/colorPrimary", focusedStroke.getAttribute("android:color"))
        val unfocusedStroke = strokes.item(1) as Element
        assertEquals("@color/bg", unfocusedStroke.getAttribute("android:color"))

        // Both branches fill with the same 87%-alpha white.
        for (i in 0 until doc.getElementsByTagName("solid").length) {
            val solid = doc.getElementsByTagName("solid").item(i) as Element
            assertEquals("@color/offer_info_white", solid.getAttribute("android:color"))
        }
    }

    @Test
    fun buttonSelectorMatchesButtonSelectorDrawable() {
        val doc = parseDrawable("button_selector.xml")
        val shapes = doc.getElementsByTagName("shape")
        assertEquals(2, shapes.length)

        // Enabled (second branch): fill colorPrimary, 1dp colorPrimaryDark.
        val enabled = shapes.item(1) as Element
        assertEquals(
            "@color/colorPrimary",
            (enabled.getElementsByTagName("solid").item(0) as Element)
                .getAttribute("android:color"),
        )
        val enabledStroke = enabled.getElementsByTagName("stroke").item(0) as Element
        assertEquals("1dp", enabledStroke.getAttribute("android:width"))
        assertEquals("@color/colorPrimaryDark", enabledStroke.getAttribute("android:color"))

        // Pressed (first branch): fill colorPrimaryDark, 3dp colorPrimary.
        val pressed = shapes.item(0) as Element
        assertEquals(
            "@color/colorPrimaryDark",
            (pressed.getElementsByTagName("solid").item(0) as Element)
                .getAttribute("android:color"),
        )
        val pressedStroke = pressed.getElementsByTagName("stroke").item(0) as Element
        assertEquals("3dp", pressedStroke.getAttribute("android:width"))
        assertEquals("@color/colorPrimary", pressedStroke.getAttribute("android:color"))

        for (i in 0 until shapes.length) {
            val corners = (shapes.item(i) as Element)
                .getElementsByTagName("corners").item(0) as Element
            assertEquals("15dp", corners.getAttribute("android:radius"))
        }
    }

    @Test
    fun buttonLayoutPaddingAndMargins() {
        val doc = parseLayout("item_button.xml")
        val textView = doc.documentElement
        assertEquals("@drawable/button_selector", textView.getAttribute("android:background"))
        assertEquals("12dp", textView.getAttribute("android:padding"))
        assertEquals("20dp", textView.getAttribute("android:layout_marginTop"))
        assertEquals("5dp", textView.getAttribute("android:layout_marginLeft"))
        assertEquals("5dp", textView.getAttribute("android:layout_marginRight"))
        assertEquals("@color/white", textView.getAttribute("android:textColor"))
    }

    @Test
    fun ringDrawablesMatchLegacyRingMetrics() {
        for (name in listOf("circular.xml", "circular_full.xml")) {
            val shape = parseDrawable("$name").documentElement
            assertEquals("ring", shape.getAttribute("android:shape"))
            assertEquals("2.3", shape.getAttribute("android:innerRadiusRatio"))
            assertEquals("5dp", shape.getAttribute("android:thickness"))
        }
        val arc = parseDrawable("circular.xml").documentElement
        assertEquals(
            "@color/colorPrimary",
            (arc.getElementsByTagName("solid").item(0) as Element)
                .getAttribute("android:color"),
        )
        val track = parseDrawable("circular_full.xml").documentElement
        assertEquals(
            "@color/lightGrey",
            (track.getElementsByTagName("solid").item(0) as Element)
                .getAttribute("android:color"),
        )
        assertEquals(2.3f, LegacyRingMetrics.InnerRadiusRatio, 1e-6f)
        assertEquals(5f, LegacyRingMetrics.Thickness.value, 0.001f)
        assertEquals(100f, LegacyRingMetrics.RingSize.value, 0.001f)
    }

    @Test
    fun cardCornerRadiusAndElevationMatchTheCalculatorLayout() {
        val doc = parseLayout("content_emi_calculator.xml")
        val cardViews = doc.getElementsByTagName("androidx.cardview.widget.CardView")
        assertTrue("expected at least one CardView", cardViews.length > 0)
        for (i in 0 until cardViews.length) {
            val card = cardViews.item(i) as Element
            assertEquals("8dp", card.getAttribute("app:cardCornerRadius"))
            assertEquals("8dp", card.getAttribute("app:cardElevation"))
            assertEquals("12dp", card.getAttribute("android:layout_margin"))
        }
    }

    @Test
    fun historyCardIsFiveDpAndFiveDpElevation() {
        val historyDoc = parseLayout("item_emi_history.xml")
        val card = historyDoc.documentElement
        assertEquals("5dp", card.getAttribute("app:cardCornerRadius"))
        assertEquals("5dp", card.getAttribute("android:elevation"))

        val rate = doc(historyDoc, "tvRate")
        assertEquals("70dp", rate.getAttribute("android:layout_width"))
        assertEquals("70dp", rate.getAttribute("android:layout_height"))
        assertEquals("@drawable/circle_green", rate.getAttribute("android:background"))
        assertEquals("@color/white", rate.getAttribute("android:textColor"))
    }

    @Test
    fun circleBadgeColoursMatchTheirDrawables() {
        val colors = parseColorsXml()
        for ((drawable, expected) in listOf(
            "circle_green.xml" to LegacyColors.GreenDescent,
            "circle_blue.xml" to LegacyColors.BlueDescent,
            "circle_red.xml" to LegacyColors.RedDescent,
        )) {
            // These are `<selector><item><shape>` wrappers, so the shape is a
            // grandchild of the document element.
            val shape = parseDrawable(drawable).getElementsByTagName("shape").item(0) as Element
            assertEquals("oval", shape.getAttribute("android:shape"))
            val size = shape.getElementsByTagName("size").item(0) as Element
            assertEquals("150dp", size.getAttribute("android:width"))
            val colorRef = (shape.getElementsByTagName("solid").item(0) as Element)
                .getAttribute("android:color")
            val resolved = colors[colorRef.removePrefix("@color/")]
                ?: error("$colorRef not found in colors.xml")
            assertEquals(
                "$drawable colour drifted",
                expected,
                androidx.compose.ui.graphics.Color(
                    java.lang.Long.parseLong(resolved.removePrefix("#"), 16),
                ),
            )
        }
    }

    @Test
    fun dividerAndRulesAreOneDp() {
        val doc = parseLayout("content_emi_calculator.xml")
        val views = doc.getElementsByTagName("View")
        var checked = 0
        for (i in 0 until views.length) {
            val view = views.item(i) as Element
            if (view.getAttribute("android:background") == "@color/divider") {
                assertEquals("1dp", view.getAttribute("android:layout_height"))
                checked++
            }
        }
        assertTrue("expected at least one 1dp divider rule", checked > 0)
    }

    private fun doc(document: org.w3c.dom.Document, id: String): Element {
        val nodes = document.getElementsByTagName("*")
        for (i in 0 until nodes.length) {
            val element = nodes.item(i) as Element
            if (element.getAttribute("android:id") == "@+id/$id") return element
        }
        error("no view with id $id")
    }

    private fun parseDrawable(name: String) = parse(locateResourceFile("drawable/$name"))

    private fun parseLayout(name: String) = parse(locateResourceFile("layout/$name"))

    private fun parse(file: File) =
        DocumentBuilderFactory.newInstance()
            .apply { isNamespaceAware = false }
            .newDocumentBuilder()
            .parse(file)

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

    private companion object {
        private const val MAX_ALIAS_HOPS = 8
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
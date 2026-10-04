package com.horizonlabs.financialcalculator.core.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class InputFieldVisibilityTest {

    private fun condition(
        field: String,
        operator: VisibilityOperator,
        value: String
    ) = VisibilityCondition(field, operator, value)

    private fun editText(key: String, visibleWhen: VisibilityCondition?) =
        InputFieldConfig.EditText(key = key, label = key, inputType = InputDataType.DECIMAL, visibleWhen = visibleWhen)

    @Test
    fun `field without a condition is always visible`() {
        assertTrue(editText("principal", null).isVisible(emptyMap()))
        assertTrue(editText("principal", null).isVisible(mapOf("principal" to "5")))
    }

    @Test
    fun `string equality drives spinner style visibility`() {
        val field = editText("cessRate", condition("supplyType", VisibilityOperator.EQ, "INTER"))
        assertFalse(field.isVisible(mapOf("supplyType" to "INTRA")))
        assertTrue(field.isVisible(mapOf("supplyType" to "INTER")))
        assertFalse(field.isVisible(emptyMap()))
    }

    @Test
    fun `comparison is case insensitive`() {
        val field = editText("x", condition("supplyType", VisibilityOperator.EQ, "inter"))
        assertTrue(field.isVisible(mapOf("supplyType" to "INTER")))
        assertTrue(field.isVisible(mapOf("supplyType" to "  Inter  ")))
    }

    @Test
    fun `numeric operators compare as numbers not strings`() {
        val gt = editText("x", condition("tenure", VisibilityOperator.GT, "10"))
        assertFalse(gt.isVisible(mapOf("tenure" to "5")))
        assertTrue(gt.isVisible(mapOf("tenure" to "50")))

        val gte = editText("x", condition("tenure", VisibilityOperator.GTE, "10"))
        assertTrue(gte.isVisible(mapOf("tenure" to "10")))
        assertFalse(gte.isVisible(mapOf("tenure" to "9.99")))

        val lt = editText("x", condition("tenure", VisibilityOperator.LT, "10"))
        assertTrue(lt.isVisible(mapOf("tenure" to "9")))
        assertFalse(lt.isVisible(mapOf("tenure" to "10")))

        val lte = editText("x", condition("tenure", VisibilityOperator.LTE, "10"))
        assertTrue(lte.isVisible(mapOf("tenure" to "10")))
    }

    /**
     * The raw text of an input is kept as a String, so ordering conditions must
     * still work on numeric fields.
     */
    @Test
    fun `numeric operators work when values are held as text`() {
        val field = editText("x", condition("rate", VisibilityOperator.GTE, "8.5"))
        assertTrue(field.isVisible(mapOf("rate" to "8.5")))
        assertFalse(field.isVisible(mapOf("rate" to "8.25")))
    }

    @Test
    fun `ordering hides the field when either side is not numeric`() {
        val field = editText("x", condition("supplyType", VisibilityOperator.GT, "5"))
        assertFalse(field.isVisible(mapOf("supplyType" to "INTER")))
        assertFalse(field.isVisible(mapOf("supplyType" to "")))
    }

    @Test
    fun `not equals stays visible when the source field is unanswered`() {
        val field = editText("x", condition("supplyType", VisibilityOperator.NE, "INTER"))
        assertTrue(field.isVisible(emptyMap()))
        assertFalse(field.isVisible(mapOf("supplyType" to "INTER")))
    }

    @Test
    fun `contains does a substring match`() {
        val field = editText("x", condition("tags", VisibilityOperator.CONTAINS, "state"))
        assertTrue(field.isVisible(mapOf("tags" to "inter-state supply")))
        assertFalse(field.isVisible(mapOf("tags" to "intra")))
    }

    @Test
    fun `hidden fields are dropped from the value map given to the engine`() {
        val fields = listOf(
            editText("principal", null),
            editText("cessRate", condition("supplyType", VisibilityOperator.EQ, "INTER"))
        )
        val values = mapOf("principal" to "1000", "cessRate" to "5", "supplyType" to "INTRA")

        val visible = fields.visibleValues(values, extra = mapOf("calculatorId" to "GST"))

        assertEquals(setOf("principal", "calculatorId"), visible.keys)
    }

    @Test
    fun `hidden field reappears once its condition holds`() {
        val fields = listOf(editText("cessRate", condition("supplyType", VisibilityOperator.EQ, "INTER")))
        val intra = fields.visibleValues(mapOf("cessRate" to "5", "supplyType" to "INTRA"))
        val inter = fields.visibleValues(mapOf("cessRate" to "5", "supplyType" to "INTER"))

        assertFalse("cessRate" in intra)
        assertEquals("5", inter["cessRate"])
    }
}

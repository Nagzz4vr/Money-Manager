package com.nagz.money_manager.utils

import android.graphics.Color
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener

object PlotUtils {

    private var lastSelectedLabel: String? = null
    private var showPercentage = false

    fun setupIncomePieChart(
        pieChart: PieChart,
        categoryData: Map<String, Float>
    ) {

        if (categoryData.isEmpty()) {
            pieChart.clear()
            pieChart.invalidate()
            return
        }

        val entries = categoryData.map { (category, amount) ->
            PieEntry(amount, category)
        }

        val chartColors = arrayListOf(
            Color.parseColor("#7F35FF"),
            Color.parseColor("#9D66FF"),
            Color.parseColor("#5C19DB"),
            Color.parseColor("#BB86FC"),
            Color.parseColor("#03DAC5"),
            Color.parseColor("#3700B3"),
            Color.parseColor("#CF6679"),
            Color.parseColor("#018786"),
            Color.parseColor("#FFB74D")
        )

        // ---------- DATASET ----------
        val dataSet = PieDataSet(entries, "").apply {
            colors = chartColors
            sliceSpace = 3f

            xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
            yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
            valueLinePart1OffsetPercentage = 80f
            valueLinePart1Length = 0.5f
            valueLinePart2Length = 0.4f
            valueLineWidth = 2f
            valueLineColor = Color.WHITE

            selectionShift = 15f
            valueTextColor = Color.WHITE
            valueTextSize = 12f

            setDrawValues(false) // 🔴 prevents overlap
        }

        // ---------- TOTAL ----------
        val totalAmount = entries.sumOf { it.value.toDouble() }.toFloat()

        // ---------- PIE CHART ----------
        pieChart.apply {

            data = PieData(dataSet).apply {
                setValueFormatter(DefaultValueFormatter(0))
                setDrawValues(false)
            }

            description.isEnabled = false

            // Donut
            isDrawHoleEnabled = true
            holeRadius = 75f
            setHoleColor(Color.parseColor("#151318"))
            setTransparentCircleRadius(78f)
            setTransparentCircleColor(Color.WHITE)
            setTransparentCircleAlpha(15)

            // Center text
            setCenterTextColor(Color.WHITE)
            setCenterTextSize(16f)
            centerText = "Total\n₹${totalAmount.toInt()}"

            // Interaction
            isRotationEnabled = true
            setHighlightPerTapEnabled(true)
            setTouchEnabled(true)
            setDragDecelerationFrictionCoef(0.95f)

            setExtraOffsets(25f, 10f, 25f, 10f)

            // Legend
            legend.apply {
                isEnabled = true
                textColor = Color.WHITE
                textSize = 12f
                form = Legend.LegendForm.CIRCLE
                orientation = Legend.LegendOrientation.HORIZONTAL
                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                isWordWrapEnabled = true
                yOffset = 5f
            }

            setDrawEntryLabels(false)

            invalidate()
        }

        // ---------- TAP LOGIC (₹ ↔ %) ----------
        pieChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {

            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (e !is PieEntry) return

                showPercentage = if (e.label == lastSelectedLabel) {
                    !showPercentage
                } else {
                    false
                }

                lastSelectedLabel = e.label

                val text = if (showPercentage) {
                    val percent = (e.value / totalAmount) * 100f
                    "${e.label}\n${"%.1f".format(percent)}%"
                } else {
                    "${e.label}\n₹${e.value.toInt()}"
                }

                pieChart.centerText = text
                pieChart.invalidate()
            }

            override fun onNothingSelected() {
                lastSelectedLabel = null
                showPercentage = false
                pieChart.centerText = "Total\n₹${totalAmount.toInt()}"
                pieChart.invalidate()
            }
        })
    }
}

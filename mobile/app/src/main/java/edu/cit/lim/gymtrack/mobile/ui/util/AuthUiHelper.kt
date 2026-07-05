package edu.cit.lim.gymtrack.mobile.ui.util

import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import edu.cit.lim.gymtrack.mobile.databinding.ItemBrandStatBinding
import edu.cit.lim.gymtrack.mobile.databinding.LayoutAuthBrandPanelBinding

data class BrandStatData(val value: String, val label: String)

fun LayoutAuthBrandPanelBinding.setupBrandPanel(
    line1: String,
    line2: String,
    highlight: String,
    tagline: String,
    stats: List<BrandStatData>,
    eyebrow: String? = null
) {
    if (eyebrow.isNullOrBlank()) {
        brandEyebrow.visibility = View.GONE
    } else {
        brandEyebrow.visibility = View.VISIBLE
        brandEyebrow.text = eyebrow.uppercase()
    }
    brandLine1.text = line1.uppercase()
    brandLine2.text = line2.uppercase()
    brandHighlight.text = highlight.uppercase()
    brandTagline.text = tagline
    brandStatsRow.removeAllViews()
    val inflater = LayoutInflater.from(root.context)
    stats.forEach { stat ->
        val itemBinding = ItemBrandStatBinding.inflate(inflater, brandStatsRow, false)
        itemBinding.statValue.text = stat.value
        itemBinding.statLabel.text = stat.label.uppercase()
        brandStatsRow.addView(itemBinding.root)
    }
}

fun TextView.showError(message: String?) {
    if (message.isNullOrBlank()) {
        visibility = View.GONE
        text = ""
    } else {
        visibility = View.VISIBLE
        text = message
    }
}

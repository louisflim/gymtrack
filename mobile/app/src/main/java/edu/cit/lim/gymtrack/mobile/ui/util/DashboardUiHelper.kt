package edu.cit.lim.gymtrack.mobile.ui.util

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import edu.cit.lim.gymtrack.mobile.R
import edu.cit.lim.gymtrack.mobile.databinding.ItemBrandStatBinding
import edu.cit.lim.gymtrack.mobile.databinding.ItemNavTabBinding

fun LinearLayout.setupNavTabs(
    role: String,
    activeTab: String,
    onTabSelected: (String) -> Unit
) {
    removeAllViews()
    val inflater = LayoutInflater.from(context)
    DashboardUiCopy.navTabsForRole(role).forEach { tab ->
        val tabBinding = ItemNavTabBinding.inflate(inflater, this, false)
        tabBinding.navIcon.text = tab.icon
        tabBinding.navLabel.text = tab.label.uppercase()
        val selected = tab.id == activeTab
        tabBinding.root.isSelected = selected
        val textColor = if (selected) R.color.gymtrack_background else R.color.gymtrack_text_primary
        val labelColor = if (selected) R.color.gymtrack_background else R.color.gymtrack_text_muted
        tabBinding.navIcon.setTextColor(ContextCompat.getColor(context, textColor))
        tabBinding.navLabel.setTextColor(ContextCompat.getColor(context, labelColor))
        tabBinding.root.setOnClickListener { onTabSelected(tab.id) }
        addView(tabBinding.root)
    }
}

fun TextView.applyStatusBadge(status: String?) {
    val normalized = status?.uppercase() ?: "NONE"
    text = normalized
    val (bg, color) = when (normalized) {
        "ACTIVE" -> R.drawable.bg_status_active to R.color.gymtrack_accent
        "EXPIRING", "EXPIRING_SOON" -> R.drawable.bg_status_expiring to R.color.status_expiring_text
        "EXPIRED" -> R.drawable.bg_status_expired to R.color.gymtrack_error
        else -> R.drawable.bg_status_none to R.color.gymtrack_text_secondary
    }
    setBackgroundResource(bg)
    setTextColor(ContextCompat.getColor(context, color))
    setPadding(16, 8, 16, 8)
}

fun ImageView.loadBase64Qr(base64: String?) {
    if (base64.isNullOrBlank()) {
        setImageDrawable(null)
        return
    }
    runCatching {
        val bytes = Base64.decode(base64, Base64.DEFAULT)
        setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.size))
    }
}

fun LinearLayout.setupHeroStats() {
    removeAllViews()
    val inflater = LayoutInflater.from(context)
    listOf(
        BrandStatData("01", "Profile"),
        BrandStatData("02", "Membership"),
        BrandStatData("03", "Access")
    ).forEach { stat ->
        val item = ItemBrandStatBinding.inflate(inflater, this, false)
        item.statValue.text = stat.value
        item.statLabel.text = stat.label.uppercase()
        addView(item.root)
    }
}

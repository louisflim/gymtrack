package edu.cit.lim.gymtrack.mobile.ui.model

data class AccountFieldValues(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = ""
)

data class KpiItem(
    val label: String,
    val value: String
)

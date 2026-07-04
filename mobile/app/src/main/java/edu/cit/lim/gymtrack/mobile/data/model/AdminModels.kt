package edu.cit.lim.gymtrack.mobile.data.model

import java.math.BigDecimal

data class DashboardStatsResponse(
    val totalMembers: Long,
    val activeSubscriptions: Long,
    val expiredMemberships: Long,
    val todayCheckIns: Long,
    val totalPaymentsCollected: BigDecimal
)

data class StaffResponse(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val active: Boolean
)

data class StaffUpdateRequest(
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val active: Boolean? = null
)

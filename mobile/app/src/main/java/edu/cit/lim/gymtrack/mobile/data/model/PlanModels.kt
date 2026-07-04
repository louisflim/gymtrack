package edu.cit.lim.gymtrack.mobile.data.model

import java.math.BigDecimal

data class PlanRequest(
    val name: String,
    val durationDays: Int,
    val price: BigDecimal,
    val active: Boolean = true
)

data class PlanResponse(
    val id: Long,
    val name: String,
    val durationDays: Int,
    val price: BigDecimal,
    val active: Boolean,
    val createdAt: String?
)

data class MemberResponse(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val active: Boolean,
    val planName: String?,
    val membershipStatus: String,
    val membershipEndDate: String?
)

data class MemberUpdateRequest(
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val active: Boolean? = null
)

data class MembershipResponse(
    val id: Long?,
    val planId: Long?,
    val planName: String?,
    val startDate: String?,
    val endDate: String?,
    val status: String,
    val gymId: Long? = null,
    val gymName: String? = null,
    val firstCheckInCompleted: Boolean = false,
    val nextStep: String? = null
)

data class MemberGymScanResponse(
    val action: String,
    val message: String,
    val gymName: String?,
    val membershipStatus: String?,
    val planName: String?,
    val firstCheckInCompleted: Boolean,
    val nextStep: String?
)

data class AssignPlanRequest(
    val memberId: Long,
    val planId: Long
)

data class CheckoutRequest(
    val planId: Long
)

data class CheckoutResponse(
    val paymentId: Long,
    val checkoutUrl: String,
    val status: String
)

data class PaymentResponse(
    val id: Long,
    val memberName: String?,
    val planName: String,
    val amount: BigDecimal,
    val currency: String,
    val status: String,
    val paymentMethod: String?,
    val createdAt: String,
    val paidAt: String?
)

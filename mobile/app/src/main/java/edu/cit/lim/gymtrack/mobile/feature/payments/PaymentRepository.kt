package edu.cit.lim.gymtrack.mobile.feature.payments

import edu.cit.lim.gymtrack.mobile.data.model.CheckoutRequest
import edu.cit.lim.gymtrack.mobile.data.model.CheckoutResponse
import edu.cit.lim.gymtrack.mobile.data.model.PaymentResponse
import edu.cit.lim.gymtrack.mobile.data.model.PaymentStatusResponse
import edu.cit.lim.gymtrack.mobile.data.remote.ApiResponses
import edu.cit.lim.gymtrack.mobile.data.remote.ApiService

class PaymentRepository(private val apiService: ApiService) {

    suspend fun checkout(planId: Long): CheckoutResponse =
        // Deep-link returns for real PayMongo; mock checkouts confirm in-app and never open a browser.
        ApiResponses.unwrap(
            apiService.checkout(
                CheckoutRequest(
                    planId = planId,
                    successUrl = "gymtrack://payment/success",
                    cancelUrl = "gymtrack://payment/cancel"
                )
            )
        )

    suspend fun paymentMode() =
        ApiResponses.unwrap(apiService.paymentMode())

    suspend fun myPayments(): List<PaymentResponse> =
        ApiResponses.unwrap(apiService.myPayments())

    suspend fun allPayments(): List<PaymentResponse> =
        ApiResponses.unwrap(apiService.allPayments())

    suspend fun confirmMockPayment(reference: String) {
        ApiResponses.unwrap(apiService.confirmMockPayment(reference))
    }

    suspend fun paymentStatus(reference: String): PaymentStatusResponse =
        ApiResponses.unwrap(apiService.paymentStatus(reference))
}

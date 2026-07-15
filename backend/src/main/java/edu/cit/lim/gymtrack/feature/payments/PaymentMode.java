package edu.cit.lim.gymtrack.feature.payments;

/**
 * Active payment gateway mode for GymTrack.
 * MOCK = school demo (no PayMongo keys), TEST = PayMongo test keys, LIVE = production keys.
 */
public enum PaymentMode {
    MOCK,
    TEST,
    LIVE
}

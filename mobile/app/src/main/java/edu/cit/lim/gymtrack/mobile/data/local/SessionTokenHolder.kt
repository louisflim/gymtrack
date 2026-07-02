package edu.cit.lim.gymtrack.mobile.data.local

object SessionTokenHolder {
    @Volatile
    var token: String? = null
}

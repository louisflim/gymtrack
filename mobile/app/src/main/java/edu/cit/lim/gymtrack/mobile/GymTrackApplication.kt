package edu.cit.lim.gymtrack.mobile

import android.app.Application
import edu.cit.lim.gymtrack.mobile.data.local.SessionDataStore
import edu.cit.lim.gymtrack.mobile.data.local.SessionTokenHolder
import edu.cit.lim.gymtrack.mobile.data.remote.RetrofitClient
import edu.cit.lim.gymtrack.mobile.data.repository.AttendanceRepository
import edu.cit.lim.gymtrack.mobile.data.repository.AuthRepository
import edu.cit.lim.gymtrack.mobile.data.repository.GymRepository
import edu.cit.lim.gymtrack.mobile.feature.auth.login.LoginRepository
import edu.cit.lim.gymtrack.mobile.feature.auth.registration.RegistrationRepository
import edu.cit.lim.gymtrack.mobile.feature.auth.staff.StaffCreationRepository
import edu.cit.lim.gymtrack.mobile.feature.members.MemberRepository
import edu.cit.lim.gymtrack.mobile.feature.plans.PlanRepository
import kotlinx.coroutines.runBlocking

class GymTrackApplication : Application() {

    lateinit var authRepository: AuthRepository
        private set
    lateinit var attendanceRepository: AttendanceRepository
        private set
    lateinit var gymRepository: GymRepository
        private set
    lateinit var registrationRepository: RegistrationRepository
        private set
    lateinit var loginRepository: LoginRepository
        private set
    lateinit var staffCreationRepository: StaffCreationRepository
        private set
    lateinit var planRepository: PlanRepository
        private set
    lateinit var memberRepository: MemberRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val sessionDataStore = SessionDataStore(this)
        val api = RetrofitClient.apiService
        authRepository = AuthRepository(sessionDataStore)
        registrationRepository = RegistrationRepository(sessionDataStore, api)
        loginRepository = LoginRepository(sessionDataStore, api)
        staffCreationRepository = StaffCreationRepository(api)
        planRepository = PlanRepository(api)
        memberRepository = MemberRepository(api)
        attendanceRepository = AttendanceRepository(api)
        gymRepository = GymRepository(api)

        runBlocking {
            val session = authRepository.currentSession()
            SessionTokenHolder.token = session.token.ifBlank { null }
        }

        RetrofitClient.setTokenProvider { SessionTokenHolder.token }
    }
}

package edu.cit.lim.gymtrack.util;

import edu.cit.lim.gymtrack.entity.Membership;
import edu.cit.lim.gymtrack.entity.MembershipStatus;

import java.time.LocalDate;

public final class MembershipStatusUtil {

    private static final int EXPIRING_SOON_DAYS = 7;

    private MembershipStatusUtil() {}

    public static MembershipStatus computeStatus(LocalDate endDate) {
        if (endDate == null) {
            return MembershipStatus.NONE;
        }
        LocalDate today = LocalDate.now();
        if (endDate.isBefore(today)) {
            return MembershipStatus.EXPIRED;
        }
        if (!endDate.isAfter(today.plusDays(EXPIRING_SOON_DAYS))) {
            return MembershipStatus.EXPIRING_SOON;
        }
        return MembershipStatus.ACTIVE;
    }

    public static void refreshStatus(Membership membership) {
        membership.setStatus(computeStatus(membership.getEndDate()));
    }

    public static boolean allowsAttendance(MembershipStatus status) {
        return status == MembershipStatus.ACTIVE || status == MembershipStatus.EXPIRING_SOON;
    }
}

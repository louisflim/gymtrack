package edu.cit.lim.gymtrack.util;

import edu.cit.lim.gymtrack.entity.Gym;
import edu.cit.lim.gymtrack.entity.Role;
import edu.cit.lim.gymtrack.entity.User;

public final class GymGuard {

    private GymGuard() {}

    public static Gym requireStaffGym(User user) {
        if (user.getRole() != Role.ADMIN && user.getRole() != Role.STAFF) {
            throw new SecurityException("Only staff or admins can perform this action.");
        }
        Gym gym = user.getGym();
        if (gym == null) {
            throw new IllegalArgumentException("Your account is not linked to a gym.");
        }
        return gym;
    }

    public static Gym requireAdminGym(User user) {
        if (user.getRole() != Role.ADMIN) {
            throw new SecurityException("Only admins can perform this action.");
        }
        Gym gym = user.getGym();
        if (gym == null) {
            throw new IllegalArgumentException("Your account is not linked to a gym.");
        }
        return gym;
    }
}

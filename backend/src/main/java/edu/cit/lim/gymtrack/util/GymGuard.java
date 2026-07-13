package edu.cit.lim.gymtrack.util;

import edu.cit.lim.gymtrack.entity.Gym;
import edu.cit.lim.gymtrack.entity.Role;
import edu.cit.lim.gymtrack.entity.User;

public final class GymGuard {

    private GymGuard() {}

    public static Gym requireStaffGym(User user) {
        if (user.getRole() != Role.ADMIN && user.getRole() != Role.STAFF) {
            throw new SecurityException("You don't have permission to do that.");
        }
        Gym gym = user.getGym();
        if (gym == null) {
            throw new IllegalArgumentException("Your account isn't connected to a gym yet.");
        }
        return gym;
    }

    public static Gym requireAdminGym(User user) {
        if (user.getRole() != Role.ADMIN) {
            throw new SecurityException("You don't have permission to do that.");
        }
        Gym gym = user.getGym();
        if (gym == null) {
            throw new IllegalArgumentException("Your account isn't connected to a gym yet.");
        }
        return gym;
    }
}

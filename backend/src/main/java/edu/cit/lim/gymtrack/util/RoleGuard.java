package edu.cit.lim.gymtrack.util;

import edu.cit.lim.gymtrack.entity.Role;
import edu.cit.lim.gymtrack.entity.User;
import edu.cit.lim.gymtrack.repository.UserRepository;

public final class RoleGuard {

    private RoleGuard() {}

    public static User requireUser(UserRepository userRepository, String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new SecurityException("Authenticated user not found."));
    }

    public static User requireAdmin(UserRepository userRepository, String email) {
        User user = requireUser(userRepository, email);
        if (user.getRole() != Role.ADMIN) {
            throw new SecurityException("Only ADMIN can perform this action.");
        }
        return user;
    }

    public static User requireAdminOrStaff(UserRepository userRepository, String email) {
        User user = requireUser(userRepository, email);
        if (user.getRole() != Role.ADMIN && user.getRole() != Role.STAFF) {
            throw new SecurityException("Only ADMIN or STAFF can perform this action.");
        }
        return user;
    }

    public static User requireMember(UserRepository userRepository, String email) {
        User user = requireUser(userRepository, email);
        if (user.getRole() != Role.MEMBER) {
            throw new SecurityException("Only MEMBER can perform this action.");
        }
        return user;
    }
}

package edu.cit.lim.gymtrack.util;

import edu.cit.lim.gymtrack.entity.Gym;
import edu.cit.lim.gymtrack.entity.Role;
import edu.cit.lim.gymtrack.entity.User;
import edu.cit.lim.gymtrack.repository.UserRepository;

public final class GymUserLookup {

    private GymUserLookup() {}

    public static User requireUserAtGym(
            UserRepository userRepository,
            Long userId,
            Gym gym,
            Role expectedRole,
            String notFoundMessage,
            String wrongRoleMessage
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(notFoundMessage));
        if (user.getRole() != expectedRole) {
            throw new IllegalArgumentException(wrongRoleMessage);
        }
        if (user.getGym() == null || !user.getGym().getId().equals(gym.getId())) {
            throw new IllegalArgumentException(
                    expectedRole == Role.MEMBER
                            ? "Member is not enrolled at your gym. Ask them to scan your gym QR code first."
                            : "Staff account does not belong to your gym."
            );
        }
        return user;
    }

    public static void applyProfileUpdates(
            User user,
            String firstName,
            String lastName,
            String email,
            Boolean active,
            UserRepository userRepository
    ) {
        if (firstName != null && !firstName.isBlank()) {
            user.setFirstName(firstName.trim());
        }
        if (lastName != null && !lastName.isBlank()) {
            user.setLastName(lastName.trim());
        }
        if (email != null && !email.isBlank()) {
            String trimmed = email.trim();
            if (!trimmed.equalsIgnoreCase(user.getEmail()) && userRepository.existsByEmail(trimmed)) {
                throw new IllegalArgumentException("That email is already registered.");
            }
            user.setEmail(trimmed);
        }
        if (active != null) {
            user.setActive(active);
        }
    }
}

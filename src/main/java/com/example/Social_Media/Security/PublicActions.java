package com.example.Social_Media.Security;

import java.util.Set;

/**
 * Defines the set of API actions that can be called without a valid JWT access token.
 * All other actions require a valid Bearer token in the Authorization header.
 */
public class PublicActions {

    public static final Set<String> PUBLIC = Set.of(
            "loginUser",
            "users",                    // createUser / signup
            "checkUsernameAvailability",
            "refreshToken",
            "checkAppUpdate"
    );

    public static boolean isPublic(String action) {
        return action != null && PUBLIC.contains(action);
    }
}
package com.pleiades.strings;

public enum JwtRole {
    ROLE_USER("USER"),
    ROLE_ADMIN("ADMIN");


    private final String role;

    JwtRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}

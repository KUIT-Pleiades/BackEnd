package com.pleiades.strings;

import lombok.Getter;

@Getter
public enum FriendStatus {

    PENDING("pending"),
    ACCEPTED("accepted"),
    REJECTED("rejected");

    private final String status;

    FriendStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }

    public static FriendStatus fromString(String status) {
        for (FriendStatus fs : FriendStatus.values()) {
            if (fs.status.equalsIgnoreCase(status)) {
                return fs;
            }
        }
        throw new IllegalArgumentException("Invalid status: " + status);
    }
}

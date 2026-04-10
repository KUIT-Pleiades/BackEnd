CREATE TABLE fcm_tokens
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     VARCHAR(255) NOT NULL,
    token       VARCHAR(255) NOT NULL UNIQUE,
    device_type VARCHAR(20)  NOT NULL,
    created_at  DATETIME     NOT NULL,
    updated_at  DATETIME     NOT NULL,
    CONSTRAINT fk_fcm_tokens_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE notifications
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    receiver_id VARCHAR(255) NOT NULL,
    type        VARCHAR(50)  NOT NULL,
    title       VARCHAR(255) NOT NULL,
    body        TEXT         NOT NULL,
    is_read     TINYINT(1)   NOT NULL DEFAULT 0,
    created_at  DATETIME     NOT NULL,
    related_id  BIGINT,
    CONSTRAINT fk_notifications_receiver FOREIGN KEY (receiver_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE notification_settings
(
    id                      BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id                 VARCHAR(255) NOT NULL UNIQUE,
    friend_request_enabled  TINYINT(1)   NOT NULL DEFAULT 1,
    signal_enabled          TINYINT(1)   NOT NULL DEFAULT 1,
    report_reminder_enabled TINYINT(1)   NOT NULL DEFAULT 1,
    item_sold_enabled       TINYINT(1)   NOT NULL DEFAULT 1,
    station_join_enabled    TINYINT(1)   NOT NULL DEFAULT 1,
    notice_enabled          TINYINT(1)   NOT NULL DEFAULT 1,
    CONSTRAINT fk_notification_settings_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

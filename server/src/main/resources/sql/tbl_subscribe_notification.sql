create table tbl_subscribe_notification
(
    id           bigint primary key,
    subscribe_id bigint not null,
    constraint fk_notification_subscribe foreign key (id)
        references tbl_notification (id) ON DELETE CASCADE,
    constraint fk_subscribe_notification foreign key (subscribe_id)
        references tbl_subscribe (id) ON DELETE CASCADE
);


ALTER TABLE tbl_subscribe_notification
    ADD CONSTRAINT fk_subscribe_notification
        FOREIGN KEY (subscribe_id) REFERENCES tbl_subscribe (id)
            ON DELETE CASCADE;

ALTER TABLE tbl_subscribe_notification
    DROP FOREIGN KEY fk_subscribe_notification;
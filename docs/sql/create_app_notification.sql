-- Notifications only. For tracking + wallets as well, use create_recent_tables.sql
-- Run as superuser:
--   psql -U postgres -d dairymart -f docs/sql/create_app_notification.sql

CREATE SEQUENCE IF NOT EXISTS public.app_notification_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS public.app_notification (
    notificationid bigint NOT NULL,
    userid integer,
    kind varchar(255),
    title varchar(255),
    message varchar(255),
    refid bigint,
    isread boolean,
    createdon timestamp(6),
    PRIMARY KEY (notificationid)
);

GRANT USAGE, SELECT, UPDATE ON SEQUENCE public.app_notification_seq TO admin;
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE public.app_notification TO admin;

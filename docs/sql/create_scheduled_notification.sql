-- Scheduled AdminLTE broadcasts. Run as postgres against dairymart.
CREATE SEQUENCE IF NOT EXISTS public.scheduled_notification_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE IF NOT EXISTS public.scheduled_notification (
    scheduleid bigint NOT NULL,
    title varchar(255),
    message varchar(255),
    kind varchar(255),
    audience varchar(255),
    scheduledfor timestamp(6),
    createdon timestamp(6),
    released boolean,
    cancelled boolean,
    PRIMARY KEY (scheduleid)
);

GRANT USAGE, SELECT, UPDATE ON SEQUENCE public.scheduled_notification_seq TO admin;
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE public.scheduled_notification TO admin;

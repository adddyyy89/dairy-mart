-- Recent Dairy Mart tables (GPS tracking, wallets, daily ledger snapshot,
-- in-app notifications). Run as a Postgres superuser against dairymart.
-- Safe to re-run: IF NOT EXISTS. Skip any object that already exists if
-- pgAdmin reports a name clash on sequences owned by another table.
--
--   psql -U postgres -d dairymart -f docs/sql/create_recent_tables.sql

-- GPS pings (Flutter salesman tracking)
CREATE SEQUENCE IF NOT EXISTS public.tracking_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE IF NOT EXISTS public.tracking (
    trackid integer NOT NULL,
    userid integer,
    latitude double precision,
    longitude double precision,
    isactive boolean,
    "timestamp" timestamp(6),
    PRIMARY KEY (trackid)
);

GRANT USAGE, SELECT, UPDATE ON SEQUENCE public.tracking_seq TO admin;
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE public.tracking TO admin;

-- Salesman / retailer cash wallet
CREATE SEQUENCE IF NOT EXISTS public.wallet_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE IF NOT EXISTS public.userwallet (
    walletid integer NOT NULL,
    userid integer,
    balance double precision,
    outstanding double precision,
    createdon date,
    lastupdated date,
    createdby integer,
    PRIMARY KEY (walletid)
);

GRANT USAGE, SELECT, UPDATE ON SEQUENCE public.wallet_seq TO admin;
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE public.userwallet TO admin;

-- End-of-day wallet snapshot (composite key userid + date)
CREATE TABLE IF NOT EXISTS public.dailyledger (
    userid integer NOT NULL,
    "date" timestamp(6) NOT NULL,
    startingwalletbalance double precision,
    startingoutstandingbalance double precision,
    walletbalance double precision,
    outstandingbalance double precision,
    totalbalance double precision,
    lastupdated timestamp(6),
    createdby integer,
    walletid integer,
    PRIMARY KEY (userid, "date")
);

GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE public.dailyledger TO admin;

-- Order status + ledger transaction alerts
CREATE SEQUENCE IF NOT EXISTS public.app_notification_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

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

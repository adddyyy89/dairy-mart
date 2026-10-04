-- Branch crate inventory. Run as postgres against dairymart.
CREATE TABLE IF NOT EXISTS public.crate_pool (
    poolid integer NOT NULL,
    totalcrates integer,
    availableatbranch integer,
    lastupdated timestamp(6),
    PRIMARY KEY (poolid)
);

INSERT INTO public.crate_pool (poolid, totalcrates, availableatbranch, lastupdated)
VALUES (1, 0, 0, NOW())
ON CONFLICT (poolid) DO NOTHING;

GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE public.crate_pool TO admin;

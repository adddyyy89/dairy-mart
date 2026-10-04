-- Branch product stock. Run as postgres against dairymart.
CREATE TABLE IF NOT EXISTS public.branch_inventory (
    branchid integer NOT NULL,
    productid integer NOT NULL,
    quantity integer NOT NULL DEFAULT 0,
    lastupdated timestamp(6),
    PRIMARY KEY (branchid, productid)
);

GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE public.branch_inventory TO admin;

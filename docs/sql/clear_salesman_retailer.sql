-- Dairy Mart: wipe salesman (typeid=2) and retailer (typeid=3) data
-- so you can re-create users, shops, and mappings from AdminLTE.
--
-- KEEPS: admin users (typeid=1), products, brands, product types,
--        branches, cities/states, payment types, order statuses.
--
-- Run in psql or pgAdmin against the Dairy Mart database.
-- Take a backup first. Review the preview SELECTs, then run the DELETEs.

BEGIN;

CREATE TEMP TABLE tmp_sr_users AS
SELECT u.userid
FROM public."User" u
WHERE u.typeid IN (2, 3);

CREATE TEMP TABLE tmp_sr_shops AS
SELECT s.shopid, s.userid, s.addressid, s.gstid
FROM public.shop s
WHERE s.userid IN (SELECT userid FROM tmp_sr_users)
   OR s.shopid IN (
        SELECT retailerid FROM public.salesmantoretail
        WHERE salesmanid IN (SELECT userid FROM tmp_sr_users)
   );

CREATE TEMP TABLE tmp_sr_orders AS
SELECT o.orderid
FROM public."RetailOrder" o
WHERE o.retailerid IN (SELECT shopid FROM tmp_sr_shops)
   OR o.createdby IN (SELECT userid FROM tmp_sr_users);

CREATE TEMP TABLE tmp_sr_ledgers AS
SELECT l.ledgerid
FROM public.ledger l
WHERE l.salesmanid IN (SELECT userid FROM tmp_sr_users)
   OR l.retailerid IN (SELECT userid FROM tmp_sr_users);

CREATE TEMP TABLE tmp_sr_addresses AS
SELECT addressid FROM public."User" WHERE userid IN (SELECT userid FROM tmp_sr_users)
UNION
SELECT addressid FROM tmp_sr_shops WHERE addressid IS NOT NULL AND addressid > 0;

CREATE TEMP TABLE tmp_sr_gst AS
SELECT gstid FROM tmp_sr_shops WHERE gstid IS NOT NULL AND gstid > 0;

-- Preview (optional: run these before COMMIT)
-- SELECT 'users' AS kind, count(*) FROM tmp_sr_users
-- UNION ALL SELECT 'shops', count(*) FROM tmp_sr_shops
-- UNION ALL SELECT 'orders', count(*) FROM tmp_sr_orders
-- UNION ALL SELECT 'ledgers', count(*) FROM tmp_sr_ledgers;

DELETE FROM public.salesmanorders
WHERE salesmanid IN (SELECT userid FROM tmp_sr_users)
   OR retailerorderid IN (SELECT orderid FROM tmp_sr_orders);

DELETE FROM public."RetailOrderDetails"
WHERE orderid IN (SELECT orderid FROM tmp_sr_orders);

DELETE FROM public."RetailOrder"
WHERE orderid IN (SELECT orderid FROM tmp_sr_orders);

DELETE FROM public.salesmantoretail
WHERE salesmanid IN (SELECT userid FROM tmp_sr_users)
   OR retailerid IN (SELECT shopid FROM tmp_sr_shops);

DELETE FROM public.ledgertransactions
WHERE ledgerid IN (SELECT ledgerid FROM tmp_sr_ledgers);

DELETE FROM public.dailyledger
WHERE userid IN (SELECT userid FROM tmp_sr_users);

DELETE FROM public.ledger
WHERE ledgerid IN (SELECT ledgerid FROM tmp_sr_ledgers);

DELETE FROM public.crates
WHERE userid IN (SELECT userid FROM tmp_sr_users)
   OR usertypeid IN (2, 3);

DELETE FROM public.tracking
WHERE userid IN (SELECT userid FROM tmp_sr_users);

DELETE FROM public.userwallet
WHERE userid IN (SELECT userid FROM tmp_sr_users);

DELETE FROM public."UserLogin"
WHERE userid IN (SELECT userid FROM tmp_sr_users)
   OR role IN (2, 3);

DELETE FROM public.shop
WHERE shopid IN (SELECT shopid FROM tmp_sr_shops);

DELETE FROM public."Gst" g
WHERE g.gstid IN (SELECT gstid FROM tmp_sr_gst)
  AND NOT EXISTS (
        SELECT 1 FROM public.shop s WHERE s.gstid = g.gstid
  );

DELETE FROM public."User"
WHERE userid IN (SELECT userid FROM tmp_sr_users);

DELETE FROM public."UserAddress" a
WHERE a.addressid IN (SELECT addressid FROM tmp_sr_addresses)
  AND NOT EXISTS (
        SELECT 1 FROM public."User" u WHERE u.addressid = a.addressid
  )
  AND NOT EXISTS (
        SELECT 1 FROM public.shop s WHERE s.addressid = a.addressid
  )
  AND NOT EXISTS (
        SELECT 1 FROM public.branch b WHERE b.addressid = a.addressid
  );

COMMIT;

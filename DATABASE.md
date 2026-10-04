# Database notes

Database name: **dairymart**. Application JDBC user in local properties: **admin**. Schema: **public**. Hibernate does **not** migrate schema (`spring.jpa.hibernate.ddl-auto=none`).

## Naming

Live columns are mostly **lowercase unquoted** identifiers (`userid`, `typeid`, `createdby`). The users table is quoted **`"User"`**. Entities set `@Column(name = "...")` accordingly. Do not rely on Hibernate converting `userId` → `user_id`.

## Core tables (conceptual)

| Table | Role |
| --- | --- |
| `"User"` | Accounts; `typeid` 1/2/3; `phonenumber`; password |
| `usertype` | Role lookup |
| `userlogin` | Sessions |
| `shop` | Retail stores |
| `branch` | Distribution branches |
| `product` / product type | Catalog |
| `salesmantoretail` | Salesman ↔ shop assignment |
| `retail_order` | Header; `retailer_id` = shop id; `orderstatusid` |
| `retail_order_details` | Lines (`productcode`, qty) |
| `orderstatus` | Status lookup 1–7 |
| `branch_inventory` | On-hand `(branchid, productid)` |
| `crate` | Holder balances / history per user |
| `crate_pool` | Branch crate stock, `poolid = 1` |
| `ledger` / `ledgertransactions` | Money events |
| `userwallet` | Running wallet |
| `dailyledger` | Daily snapshot `(userid, date)` |
| `tracking` | GPS pings |
| `app_notification` | Inbox + activity (`userid` −1) |
| `scheduled_notification` | Future broadcasts |
| address / `state` / `city` | Geo pickers |

Full historical DDL: `db_ddl.txt` and `docs/db_ddl.txt`. Prefer incremental files under `docs/sql/` for objects added later.

## Incremental SQL (run as postgres)

| File | Creates |
| --- | --- |
| `docs/sql/create_recent_tables.sql` | `tracking`, `userwallet`, `dailyledger`, `app_notification` |
| `docs/sql/create_app_notification.sql` | Notifications only |
| `docs/sql/create_crate_pool.sql` | `crate_pool` + seed row 1 |
| `docs/sql/create_branch_inventory.sql` | `branch_inventory` |
| `docs/sql/create_scheduled_notification.sql` | `scheduled_notification` |
| `docs/sql/clear_salesman_retailer.sql` | Data cleanup helper (destructive; read before run) |

After `CREATE`, scripts `GRANT` DML (and sequence usage) to `admin`. If `admin` still gets `permission denied for schema public`, grant usage on schema and default privileges as postgres.

## Sequences

Integer PKs typically use dedicated sequences (`user_seq`, `tracking_seq`, `wallet_seq`, `app_notification_seq`, `scheduled_notification_seq`, …). Keep sequence ownership/grants in sync when recreating tables.

## Privileges pattern

1. Superuser runs DDL.
2. `GRANT SELECT, INSERT, UPDATE, DELETE` on new tables to `admin`.
3. `GRANT USAGE, SELECT, UPDATE` on new sequences to `admin`.

## Dump module

`dairyappexceldump` uses the same JDBC URL and naming strategies. Spring Batch will create `BATCH_*` tables when `initialize-schema=always` if the user can create tables; otherwise create Batch schema as postgres.

Excel sheets skip a table if the relation is missing (42P01), so a dump can succeed on a partial schema.

## Seed / defaults in code

- Default `branchId` **7** when an order omits branch (`RetailOrderService`) and in AdminLTE settings.
- `crate_pool.poolid = 1` is the only pool row the crate service expects.
- Do not use `userid = 0` as a real person; it is reserved for system-style notifications in this product. Activity uses **−1**.

# Design document

## Purpose

Support a city/branch dairy distributor: branches hold product and crates; salesmen visit shops; shops order; cash and crates move with the goods; ops staff watch stock, money, and location from a browser.

## Roles and identities

| typeId | Role | Primary client |
| --- | --- | --- |
| 1 | Admin | AdminLTE |
| 2 | Salesman | Flutter |
| 3 | Retailer | Flutter |

- Login identity: **phone**.
- Shop is a first-class entity. Orders reference **shop id** in `retail_order.retailer_id`.
- Salesman coverage is `salesmantoretail` (salesman user ↔ shop).

## Order status machine

| Id | Name | Notes |
| --- | --- | --- |
| 1 | NEW | Default on create; no stock consume |
| 2 | CONFIRMED | First entry to 2/4/5 consumes branch stock |
| 3 | REJECTED | No consume |
| 4 | DISPATCHED | Consume if not already consumed |
| 5 | DELIVERED | Consume if not already consumed |
| 6 | RETURNED | No consume on this transition |
| 7 | CANCELLED | No consume |

AdminLTE and apps must treat a 400 on status update as a **stock (or validation) failure**, not a generic crash.

## Inventory design

- Grain: one row per `(branchId, productId)`.
- Missing row means quantity **0**, not “unlimited”.
- Admin edits quantity explicitly (`POST /inventory/set`).
- Deduction is **idempotent per order** for the first transition into the consume set {2,4,5}.
- Order lines use **productCode**; inventory uses **productId** — mapping is server-side.

## Crate design

Physical crates are not serialized. Counts move between:

1. **Branch pool** (`crate_pool`, singleton `poolid = 1`)
2. **Salesman** (`crate` rows keyed by user)
3. **Store** (shop as holder)

Movements are explicit APIs so history stays auditable. Pool `availableatbranch` and `totalcrates` must stay consistent with assign/return/add.

## Money design

- `userwallet`: running balance and outstanding per user.
- `ledger` + `ledgertransactions`: event log (payments, order-linked amounts).
- `dailyledger`: date-stamped snapshot for reporting (midnight job).

## Notifications design

- Operational inbox: `app_notification` per user.
- Broadcast audience is encoded on the broadcast/schedule payload (e.g. all salesmen).
- Activity stream is the same table with `userid = -1` so it does not clutter personal inboxes.
- Scheduling is a separate table so “not yet sent” can be cancelled.

## API design conventions

- Paths are lowercase, resource-oriented: `/retailorder/get/{id}`, `/crate/assign`.
- JSON often produced with Gson (numeric types, date fields as in DTOs).
- Validation failures: prefer `{ "message": "..." }` and HTTP 400.
- Swagger tags group screens; Basic auth is the Try-it-out default except login.

## AdminLTE UI design

- AdminLTE 4 visual shell; Dairy Mart pages under `dist/pages` and `dist/home.html`.
- Shared header/sidebar; active nav highlighting in layout JS.
- Settings persist per browser (`localStorage`), including API base so one static host can target different backends.
- Live map: Esri world imagery/streets (no third-party tile key).
- Auto-refresh is optional and interval-based (dashboard, map, header notifications).

## Flutter UI design

- Tokens from the historical Android `DESIGN.md` (primary `#1A73E8`, Inter, moderate radius).
- Role dashboards after login; bottom nav on salesman flows (dashboard, orders, deliveries, crates, ledger).
- API paths centralized in `lib/services/api_config.dart`.

## Persistence design

- PostgreSQL public schema.
- Quoted table `"User"`; most other tables lowercase (`retail_order`, `branch_inventory`).
- Sequences for integer ids (`user_seq`, `tracking_seq`, …).
- App DB user `admin` is **not** schema owner; DDL is applied as `postgres` then `GRANT` to `admin`.

## Failure and empty states

- Inventory shortfall: 400 + message, UI keeps previous status.
- Missing `crate_pool` / `branch_inventory`: APIs return 500/empty with a hint to run SQL under `docs/sql`.
- Dump job: skip absent tables; skip Drive if credentials missing.
- Tracking: no invented coordinates; map shows last known or empty.

## Security design (current)

- Transport: HTTP in local/dev; use HTTPS in any hosted environment.
- AuthN: Basic after login; login itself is a public POST.
- AuthZ: largely “authenticated vs anonymous” plus role checks in some services; several admin GETs are public for the static console. Harden `SecurityConfig` before exposing the API to the internet.

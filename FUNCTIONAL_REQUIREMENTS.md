# Application functional requirements

Audience: operations (admin), field sales, and shop owners. One backend; three client surfaces.

## Shared

- Sign in with **phone number** and password.
- See in-app notifications and unread counts.
- Error text from the API (`message` or plain string) shown to the user; no silent 400s.
- If the API is down, AdminLTE shows a server-down banner; mobile should surface the same HTTP failures.

## Admin (user type 1)

**AdminLTE** is the primary console. Flutter also has a limited admin dashboard.

| Area | Requirement |
| --- | --- |
| Dashboard | Today’s orders, counts by order status, recent activity |
| Analytics | Top products, stores, salesmen collections/payments, store×product, crate locations |
| Activity | Platform event list (notifications with userId −1) |
| Online & logins | Who is logged in; login/logout history |
| Live map | Latest salesman positions; stale pings distinguishable |
| Users | Create/maintain admin, salesman, retailer accounts |
| Salesmen / retailers | Lists and detail (including salesman crates, trail) |
| Assign retailers | Map salesman ↔ shop (`salesmantoretail`) |
| Orders | List, open detail, change status; **block** confirm/dispatch/deliver when branch stock is short |
| Ledgers | Per-user ledger and transactions |
| Notifications | Broadcast now or schedule; list/cancel pending schedules |
| Crates | Pool stock, assign to salesman, see holders; retailers cannot edit crates |
| Inventory | Per-branch on-hand quantity, editable |
| Products | Catalog and product types |
| Settings | API base URL, auto-refresh intervals, default branch, GPS stale window |
| Excel | Download dump produced by the API process (`/exceldump/download`) |

Admins must not rely on hardcoded demo users in the UI. Credentials come from `User`.

## Salesman (user type 2)

- Dashboard: assigned work, wallet/outstanding, crates assigned.
- See shops assigned to them; create/update orders for those shops.
- See product availability vs branch stock when viewing an order.
- Deliveries: move orders through dispatched/delivered when stock allows.
- Crates: send to store, take return from store, return surplus to branch. Cannot invent GPS; pings are device-reported.
- Ledger: collections and outstanding.
- Tracking: periodic location updates while the app is in use.

## Retailer (user type 3)

- Catalog and place orders against their shop (`retailer_id` = shop id).
- Order history and status.
- Ledger / wallet for their account.
- Notifications.
- **Must not** change crate pool, salesman assignment, or other shops’ inventory.

## Excel dump service

- Produce a point-in-time `DairyMartDump.xlsx` without passwords.
- Skip tables that do not exist yet (safe on partial schemas).
- Optionally upload to a Drive folder.
- Run unattended at 23:59 and on demand via `POST /dump/run`.

## Non-functional

- Java 17, PostgreSQL, API on 8080 in local default.
- No Hibernate schema auto-create in production (`ddl-auto=none`).
- CORS must allow the AdminLTE origin (localhost with any port).
- Swagger documents AdminLTE vs app groups and Basic auth; login is unauthenticated.
- Do not log or commit production secrets.

## Out of scope / known limits

- Native Android (`dairymartapp`) is legacy; new work goes to Flutter + AdminLTE.
- Some Flutter screens still need live confirmation of dashboard JSON field names (see `dairymartapp-flutter/README.md`).
- Security is HTTP Basic, not JWT; many GETs are currently `permitAll` for AdminLTE convenience. Treat the API as a **trusted LAN** unless you tighten `SecurityConfig`.

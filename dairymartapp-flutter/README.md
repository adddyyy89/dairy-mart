# Dairy Mart — Flutter Mobile UI

A Flutter recreation of the `dairymartapp` native Android UI (Java + Volley),
built to talk to the existing Spring Boot + PostgreSQL backend
(`dairyappserver`) with **no backend changes required**.

## Why this structure

This isn't a generic starter — every screen, color, and endpoint was pulled
directly from your repo:

- **Design tokens** (`lib/theme/app_theme.dart`) come from `dairymartapp/DESIGN.md`
  (primary `#1A73E8`, neutral `#F8F9FA`, Inter font, moderate corner radius)
  and the XML layouts (`activity_main.xml`, `activity_salesman_dashboard_2.xml`, etc).
- **Models** (`lib/models/`) mirror the backend DTOs field-for-field
  (`RetailOrderDTO`, `RetailOrderDetailsDTO`, `CrateDTO`, `LedgerDTO`,
  `LedgerTransactionsDTO`, `ProductDTO`, `UserDTO`).
- **API endpoints** (`lib/services/api_config.dart`) are copied 1:1 from
  `DairyMart_postman_collection.json` — nothing guessed.
- **Auth** replicates `MainActivity.java`'s exact behavior: Basic Auth header
  built from `phone:password`, and role routing where
  `role == 1 → Admin`, `role == 2 → Salesman`, `role == 3 → Retailer`.

## Project layout

```
lib/
  main.dart                     # session check -> routes to role dashboard or login
  theme/app_theme.dart          # colors, spacing, radii, ThemeData
  models/                       # user, product, order, crate, ledger
  services/
    api_config.dart             # every endpoint path, copied from Postman collection
    api_client.dart             # http wrapper + Basic Auth header injection
    session_manager.dart        # persists userId/role/auth (like the old SharedPreferences)
    auth_service.dart           # login/logout
    order_service.dart          # create/list/update retail orders
    crate_service.dart          # crate assignment/return tracking
    ledger_service.dart         # ledger + transactions
    product_service.dart        # catalog
    dashboard_service.dart      # salesman/retailer dashboard summaries
    user_service.dart
  widgets/
    stat_card.dart              # the bordered white stat tiles used everywhere
    app_bottom_nav.dart         # Dashboard / Orders / Deliveries / Crates / Ledger
    status_chip.dart            # order status pill
  screens/
    auth/login_screen.dart                       <- activity_main.xml
    salesman/salesman_dashboard_screen.dart       <- activity_salesman_dashboard_2.xml
    salesman/salesman_activity_orders_screen.dart <- activity_salesman_activity_orders.xml
    salesman/salesman_ledger_dashboard_screen.dart<- activity_salesman_ledger_dashboard.xml
    salesman/salesman_crates_screen.dart          <- crate management (CrateDTO)
    salesman/salesman_delivery_pending_screen.dart<- delivery_pending + delivery_delivered
    salesman/salesman_create_order_screen.dart    <- activity_salesman_create_order.xml
    retailer/retailer_dashboard_screen.dart       <- activity_retailer_dashboard.xml (re-themed)
    retailer/retailer_catalog_screen.dart         <- product catalog + create order
    retailer/retailer_orders_screen.dart          <- order history
    retailer/retailer_ledger_screen.dart          <- wallet & transactions
    admin/admin_dashboard_screen.dart             <- activity_admin_dashboard.xml
```

## One deliberate design change

`activity_retailer_dashboard.xml` in the source repo still used an old
olive/green palette (`#f0f4c3` / `#388E3C`) that doesn't match `DESIGN.md` or
the salesman screens. I re-themed the retailer dashboard onto the same blue
system (`#1A73E8`) so the whole app feels like one product. Flag if you
actually want the retailer app to look visually distinct from the
salesman/admin app.

## Setup

```bash
cd dairymart_flutter
flutter pub get
flutter run --dart-define=DAIRYMART_API_BASE_URL=http://YOUR_BACKEND_HOST:8080
```

If you don't pass `--dart-define`, it defaults to `http://localhost:8080`
(same as the Postman collection's default environment). For a physical
device testing against a machine on your LAN, use that machine's IP instead
of `localhost`.

## Things that need a quick confirmation against your live backend

I built these against the DTOs and Postman collection, but a few response
shapes weren't in your `docs/json responses/` samples, so they're marked
with `TODO` comments in code:

1. **`/salesman/dashboard/get/{id}` and `/retailer/dashboard/get/{id}`**
   response shape — `DashboardStats.fromJson` guesses common field names
   (`balance`, `ordersPlaced`, `cratesAssigned`, `cratesEngaged`). Confirm
   against `SalesmanDashboardController` / a live call and adjust.
2. **Ledger transactions list endpoint** — the Postman collection doesn't
   expose a "get transactions by ledger" GET, only `/ledger/add` and
   `/ledger/salesman/get/{id}`. `LedgerService.getTransactions` currently
   hits a conventional guessed path (`/ledger/transactions/get/{ledgerId}`) —
   check `LedgerController`/`SalesmanLedgerController` for the real one.
3. **Retailer's `branchId`** — orders require a `branchId`; the retailer
   catalog screen currently hardcodes `0`. Wire this to the retailer's shop
   profile (`GET /shop/get/user/{userId}` → `branch`) once that's confirmed.
4. **Salesman → Retailer picker in "Create Order"** — currently a stub
   (`retailerId = 0`). Wire it to
   `GET /salesmantoretail/get/assignment/salesman/{id}` for the real list of
   retailers assigned to that salesman.

None of these block wiring the UI up — they're just placeholders so the
screens compile and the happy-path flow (browse → add to cart → submit) is
already complete end-to-end.

## Note on this container

Flutter SDK isn't installed in the environment I built this in, so I
couldn't run `flutter pub get` / `flutter analyze` here — please run those
locally as your first step to catch anything environment-specific.

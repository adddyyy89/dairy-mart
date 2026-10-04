# Dairy Mart

Dairy Mart is a distribution system for dairy products: a **PostgreSQL** database, a **Spring Boot** API, an **AdminLTE** operations console, **Flutter** (and legacy native Android) apps for salesmen and retailers, and a separate **Excel dump** job.

This repository is a multi-module workspace, not a single Maven/Gradle parent.

## Applications

| Folder | Role |
| --- | --- |
| `dairyappserver` | REST API, HTTP Basic auth, order/crate/inventory/ledger logic |
| `AdminLTE/dist` | Browser console for admins (serve the `dist` folder) |
| `dairymartapp-flutter` | Salesman / retailer / admin mobile UI |
| `dairymartapp` | Legacy native Android client (same API; prefer Flutter) |
| `dairyappexceldump` | Nightly/on-demand Excel export and optional Google Drive upload |

## Documentation (this folder)

| Document | Contents |
| --- | --- |
| [ARCHITECTURE.md](ARCHITECTURE.md) | Components, tech stack, security, scheduled jobs |
| [DATA_FLOW.md](DATA_FLOW.md) | Login, orders, stock, crates, GPS, notifications, dump |
| [FUNCTIONAL_REQUIREMENTS.md](FUNCTIONAL_REQUIREMENTS.md) | What each role can do |
| [DESIGN.md](DESIGN.md) | Domain model, status machines, UI conventions |
| [HOW_TO_RUN.md](HOW_TO_RUN.md) | Local Postgres, server, AdminLTE, Flutter, Excel dump |
| [DATABASE.md](DATABASE.md) | Tables, SQL scripts, naming, privileges |

API reference while the server is running: [Swagger UI](http://localhost:8080/swagger-ui.html) (`/v3/api-docs`). Postman: `docs/DairyMart.postman_collection.json`.

## Quick start

1. PostgreSQL database `dairymart`, app user `admin` (see [HOW_TO_RUN.md](HOW_TO_RUN.md)).
2. `cd dairyappserver && mvn spring-boot:run` → `http://localhost:8080`.
3. Serve `AdminLTE/dist` (for example Live Server on port 5500) and sign in with an admin phone and password.
4. Point Flutter at the API with `--dart-define=DAIRYMART_API_BASE_URL=...`.

Do not commit secrets (Twilio, Google, AWS). Keep live credentials out of git. Local `application.properties` already uses a local JDBC URL; treat cloud passwords as environment-specific.

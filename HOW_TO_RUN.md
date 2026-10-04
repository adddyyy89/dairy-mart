# How to run

Prerequisites: **JDK 17**, **Maven 3.9+**, **PostgreSQL 14+**, **Flutter SDK** (for the app), a static HTTP server for AdminLTE (VS Code Live Server, `npx serve`, Python `http.server`, etc.).

## 1. Database

1. Create database `dairymart`.
2. Create a login role the apps use (local properties use user `admin` / password `admin`).
3. Restore or keep your existing schema (`db_ddl.txt` / `docs/db_ddl.txt` are historical dumps).
4. As **postgres** (or another superuser), apply incremental scripts if objects are missing. The app user usually **cannot** `CREATE` on `public`:

```text
psql -U postgres -d dairymart -f docs/sql/create_recent_tables.sql
psql -U postgres -d dairymart -f docs/sql/create_crate_pool.sql
psql -U postgres -d dairymart -f docs/sql/create_branch_inventory.sql
psql -U postgres -d dairymart -f docs/sql/create_scheduled_notification.sql
psql -U postgres -d dairymart -f docs/sql/create_app_notification.sql
```

Scripts are `IF NOT EXISTS` and grant DML to `admin`. If a sequence name already exists, skip that object and continue.

5. Confirm JDBC in `dairyappserver/src/main/resources/application.properties` (and the dump module) points at this database. **Do not** turn on `ddl-auto=update` against production.

Local URL used in-repo:

```text
jdbc:postgresql://localhost:5432/dairymart
```

## 2. API server (`dairyappserver`)

```text
cd dairyappserver
mvn spring-boot:run
```

Or run `com.dairymart.dairyappserver.DairyMartAppApplication` from the IDE.

- Base URL: `http://localhost:8080`
- Swagger: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Sign in to Try-it-out with **Authorize** (phone / password) except `POST /auth/login`.

Working directory matters for `DairyMartDump.xlsx` written by the API’s own Excel scheduler.

## 3. AdminLTE

Serve the **built static tree**, not the AdminLTE npm `src` playground:

```text
cd AdminLTE/dist
```

Examples:

```text
npx --yes serve -l 5500
```

```text
python -m http.server 5500
```

Open `http://localhost:5500/index.html` (login) then `home.html`.

1. Settings page: set **API base** to `http://localhost:8080` if it is not already.
2. Log in with an **admin** phone and password from the `User` table.
3. Default branch id (inventory/orders) defaults to **7** in settings; change if your branch ids differ.

CORS is configured for localhost with any port. If you open HTML as `file://`, origin `null` is allowed.

## 4. Flutter app

```text
cd dairymartapp-flutter
flutter pub get
```

Point at the machine that runs the API:

- **Android emulator** (host machine): `http://10.0.2.2:8080`
- **Physical device** on LAN: `http://<your-lan-ip>:8080`
- **Chrome / desktop**: `http://localhost:8080`

```text
flutter run --dart-define=DAIRYMART_API_BASE_URL=http://10.0.2.2:8080
```

If you omit `--dart-define`, the code default is a LAN IP in `lib/services/api_config.dart` (`http://10.50.90.169:8080`). Change that define for your network; `localhost` on a phone is the phone itself, not your PC.

## 5. Legacy Android (`dairymartapp`)

Open the Gradle project in Android Studio. It talks to the same API. Prefer Flutter for new UI work.

## 6. Excel dump service (`dairyappexceldump`)

Do **not** bind both Boot apps to 8080.

```text
cd dairyappexceldump
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

Or set `server.port=8081` in that module’s `application.properties`.

- Immediate run: `POST http://localhost:8081/dump/run`
- File: `DairyMartDump.xlsx` in the dump process working directory (or `dump.output.dir` if set)
- Nightly: 23:59 JVM local time
- Drive: set `google.drive.credentials.path` and a real `google.drive.folder.id`. Placeholder folder id skips upload and still writes Excel.
- Batch metadata tables: `spring.batch.jdbc.initialize-schema=always` (needs CREATE on the dump DB user or run DDL as postgres)

The API also exposes `GET /exceldump/download` for a file produced **by the API JVM**, which is a different file/process than this module.

## 7. Typical local combo

| Process | Port |
| --- | --- |
| PostgreSQL | 5432 |
| dairyappserver | 8080 |
| AdminLTE static | 5500 |
| dairyappexceldump | 8081 |
| Flutter | device / emulator |

Start Postgres → API → static AdminLTE → (optional) dump service → Flutter.

## 8. Checks after start

- `GET http://localhost:8080/test` (hidden from Swagger) returns a short string.
- Swagger loads and lists tags (Inventory, Crates, Notifications, …).
- AdminLTE login succeeds; dashboard order-status bars are not stuck at dummy 10/10.
- Inventory page loads for your branch; if 42P01, run `create_branch_inventory.sql` as postgres.
- Crate summary: if it errors, run `create_crate_pool.sql`.

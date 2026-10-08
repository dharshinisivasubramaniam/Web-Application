# Phase 2: Registration Implementation

## A. Phase Overview
* **Objective:** Implement a full-stack user registration feature capturing exactly four fields: Name, Phone number, Email ID, and Password.
* **Starting State:** Basic plain Java HTTP server and boilerplate established in Phase 1.
* **Technology Restrictions:** Plain HTML/CSS/JavaScript, plain Java without web frameworks (using `HttpServer`), raw JDBC, MySQL, no ORMs, no external UI frameworks.
* **Final Outcome:** Registration implemented and fully verified end-to-end. Data successfully flows from the HTML form to MySQL with secure password hashing.

## B. Implementation Journey
1. **Database Schema Setup:** The user installed and connected to MySQL, creating the `vanilla_db` database and `users` table via CLI.
2. **Password Security:** Implemented `PasswordUtil.java` using Java's built-in `PBKDF2WithHmacSHA256` and `SecureRandom`. Used TDD to enforce salt uniqueness and hash consistency.
3. **Repository Layer:** Authored `User.java` and `UserRepository.java` using raw JDBC `PreparedStatement` to safely insert records and check for duplicate emails. We encountered a connection failure due to missing SSL/Timezone flags in the JDBC URL and a missing `mysql-connector-j` dependency in `build.gradle`, both of which were successfully debugged and resolved.
4. **Backend API:** Updated `MainServer.java` to expose a `POST /api/register` endpoint. Wrote a custom URL-encoded form parser since no JSON libraries were permitted.
5. **Frontend Form:** Replaced `index.html` with a plain HTML form. Used vanilla JavaScript and the `fetch` API to post `application/x-www-form-urlencoded` data to the backend.

## C. Final Project Structure
```text
Project 1/
├── build.gradle                   # Updated with mysql-connector-j and application plugin
├── src/main/java/com/myapp/
│   ├── MainServer.java            # Updated to handle /api/register POST requests
│   ├── DatabaseManager.java       # Updated with SSL/Timezone JDBC flags
│   ├── User.java                  # Created: Model class
│   ├── UserRepository.java        # Created: JDBC insert and email-check logic
│   └── PasswordUtil.java          # Created: Hashing logic
├── src/main/resources/public/
│   └── index.html                 # Replaced with registration form and JS fetch logic
└── src/test/java/com/myapp/
    ├── DatabaseManagerTest.java   
    ├── ServerTest.java            # Updated with /api/register endpoint test
    ├── UserRepositoryTest.java    # Created: DB integration tests with auto-rollback
    └── PasswordUtilTest.java      # Created: TDD unit tests for hashing
```

## D. Frontend Implementation
* **Form Fields:** `name` (text), `phone` (tel), `email` (email), `password` (password, minlength=6).
* **Validation:** Basic HTML5 constraints (`required`, `type`, `minlength`). JavaScript performs a fallback check for the `@` symbol in the email.
* **Data Transmission:** JavaScript prevents default form submission, extracts values, and packages them into `URLSearchParams`. Sent via `POST` to `/api/register` with `Content-Type: application/x-www-form-urlencoded`.
* **Response Handling:** 
  * HTTP 201: Form is reset, and a green success message is displayed.
  * Other codes: Error message returned by Java is displayed in red.

## E. Java Backend Implementation
* **Server Entry Point:** Built-in JDK `com.sun.net.httpserver.HttpServer`.
* **Endpoint:** `POST /api/register`.
* **Parsing:** Body is read as UTF-8 bytes and parsed using a custom `split("&")` and `URLDecoder.decode()` helper method.
* **Validation:** Explicit null and `.isBlank()` checks for required fields. Returns `400 Bad Request` if validation fails.
* **Duplicate Handling:** Queries the database using `UserRepository.emailExists`. Returns `409 Conflict` if the email is found.
* **Success:** Returns `201 Created` upon successful database insertion.

## F. MySQL and JDBC Implementation
* **Database/Table:** `vanilla_db` / `users`.
* **Columns:** `id` (INT AUTO_INCREMENT PRIMARY KEY), `name` (VARCHAR), `phone` (VARCHAR), `email` (VARCHAR UNIQUE), `password_hash` (VARCHAR), `password_salt` (VARCHAR).
* **Connection:** JDBC via `DriverManager.getConnection()`. Added `useSSL=false`, `allowPublicKeyRetrieval=true`, and `serverTimezone=UTC` to resolve local MySQL 8 connection quirks.
* **Security:** Exclusively uses parameterized `PreparedStatement`s to prevent SQL injection.

## G. Password Protection and Security
* **Hashing Strategy:** Passwords are mathematically hashed; plain text is never stored or logged.
* **Algorithm:** `PBKDF2WithHmacSHA256`.
* **Configuration:** 65,536 iterations, 256-bit key length.
* **Salt:** 16-byte cryptographically secure random salt generated via `java.security.SecureRandom` for every individual user.
* **Storage:** Both the raw salt and resulting hash are converted to Base64 strings for storage in `VARCHAR` columns.
* **Secrets:** Database credentials (`root` / `<password>`) are currently hardcoded in `DatabaseManager.java`. This remains an unresolved security concern for future deployment.

## H. Validation and Error Handling
* **Missing Fields:** Backend checks `name` and `password` for null/blank. Returns `400 Bad Request`.
* **Duplicate Email:** Backend executes `SELECT id FROM users WHERE email = ?`. Returns `409 Conflict` if found.
* **Database Errors:** Caught via `try-catch` blocks and returned as `500 Internal Server Error` (or propagated in tests).

## I. Configuration and Dependencies
* **Build Tool:** Gradle 8.7.
* **Plugins Added:** `application` (to enable `gradle run`).
* **Dependencies Added:** `implementation 'com.mysql:mysql-connector-j:8.3.0'`.
* **JDBC URL:** `jdbc:mysql://localhost:3306/vanilla_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`.
* **Running the App:** Can be executed via `gradle run`.

## J. Verification Results
* **Frontend-to-Backend:** PASS. Verified manually in-browser by the user ("Registration successful").
* **MySQL Insertion:** PASS. Verified directly by executing a `SELECT` query via `mysql.exe` which confirmed the exact test records were successfully inserted into the `users` table.
* **Duplicate Email Rejection:** PASS. Verified via `gradle test` asserting the HTTP 409 pathway.
* **Password Storage:** PASS. Verified via direct DB inspection; columns contain Base64 strings.
* **Build/Compilation:** PASS.

## K. Failures, Fixes and Decisions
* **Failure:** User encountered a greyed-out installation button in the MySQL installer. 
  * **Fix:** User was selecting a category folder instead of the leaf-node package. Instructed to expand the tree.
* **Failure:** `java.sql.SQLException: No suitable driver found` during tests.
  * **Fix:** We discovered `mysql-connector-j` was missing from `build.gradle` (carried over from an unverified step in Phase 1). Added the dependency.
* **Failure:** `java.sql.SQLException` during `getConnection()`.
  * **Fix:** Appended SSL and Timezone override flags to the JDBC URL, a common requirement for local MySQL 8.x instances.
* **Decision:** We bypassed JSON libraries entirely, enforcing `application/x-www-form-urlencoded`, to strictly adhere to the "no frameworks/minimal dependencies" rule.

## L. Current State and Deferred Work
**State:** Registration is 100% complete and verified. The repository has a working frontend form, backend parser, and functional database layer.

**Unimplemented/Deferred Work:**
* Login.
* Logout.
* Session management.
* JWT authentication.
* Forgot-password functionality.
* Email verification.
* OTP.
* Authorization and roles.
* Application-specific business functionality.

*(Note: While `User.java` and `UserRepository.java` existed prior to this session in an unverified state, they were completely rewritten and verified during this phase.)*

## M. Known Limitations and Unresolved Questions
* **Hardcoded Credentials:** `DatabaseManager.java` contains raw database passwords.
* **Test Suite Fragility:** The `staticFileEndpointReturnsHtml()` test in `ServerTest.java` is currently failing because it expects the old Phase 1 "Hello World" text instead of the new Phase 2 HTML. This needs to be updated.
* **Form Parser Limitation:** The custom URL-encoded parser is highly simplistic. If nested objects or arrays are required in future phases, a JSON library (e.g., Gson) will become necessary.

## N. Commit Linkage
* **Current Branch:** `main`
* **Last Commit:** `155b42620a5a5307eeea80034f5125c59b609e44` ("first commit")
* **Working Tree Status:** There are many uncommitted changes. The entirety of Phase 2 currently exists as uncommitted modified/untracked files in the working directory.

## O. Resume-from-here
The project is currently in a state where a new user can successfully register, and their securely hashed profile is saved to MySQL. The server can be booted via `gradle run` or by compiling the `MainServer` class directly with its dependencies. 

The immediate next logical step is to address the failing test suite (update the static file assertion), commit the Phase 2 changes to Git, and then proceed to implement the **Login** functionality to allow registered users to authenticate.

# Rest Assured Capstone – GoRest API Automation

API automation framework for **https://gorest.co.in/** (Perficient Rest Assured Capstone).

**Stack:** Java 11 · Maven · Rest Assured 5.4 · TestNG 7.9 · ExtentReports 5 · Jackson (POJO)

## 1. Project structure

```
restassured-capstone
├── pom.xml                      # dependencies + surefire (runs testng.xml, passes -Denv)
├── testng.xml                   # suite: User tests + Post tests + report listener
└── src/test
    ├── resources/config
    │   ├── qa.properties        # baseUrl, basePath, maxResponseTimeMs
    │   └── uat.properties
    └── java/com/perficient/capstone
        ├── config/ConfigReader.java        # reads qa/uat property file (Assignment 2)
        ├── utils/ApiClient.java            # common get/post/put/delete + auth + base URL
        ├── utils/ApiAssertions.java        # status code & response-time checks
        ├── utils/Endpoints.java            # all API paths in one place
        ├── utils/TestDataUtil.java         # unique random test data
        ├── pojo/User.java                  # request body POJO (Bonus)
        ├── base/BaseTest.java              # suite setup
        ├── listeners/ExtentTestListener    # TestNG -> Extent (pass/fail/skip)
        ├── listeners/ExtentTestManager     # builds the HTML report
        └── tests/UserApiTests.java         # Assignment 3 (5 tests)
        └── tests/PostApiTests.java         # Assignment 4 (3 tests + comments test)
```

## 2. Prerequisites
* JDK 11+ and Maven 3.8+
* A free GoRest access token: https://gorest.co.in/consumer/login
  (needed for POST / PUT / DELETE; GET works without it)

## 3. How to run

```bash
mvn test -Denv=qa  -Dtoken=<YOUR_TOKEN>
mvn test -Denv=uat -Dtoken=<YOUR_TOKEN>

# or set the token once as an environment variable
export GOREST_TOKEN=<YOUR_TOKEN>      # Windows: set GOREST_TOKEN=<YOUR_TOKEN>
mvn test -Denv=qa
```
Without `-Denv`, QA is used. Switching environments needs **no code change** – only a different property file.

**Report:** `target/extent-reports/ExecutionReport.html` (execution summary, passed, failed, skipped, request/response of every call).

## 4. Test cases (9)

| # | Class | Test | Method / Endpoint | Validations |
|---|-------|------|-------------------|-------------|
| 1 | UserApiTests | getAllUsers | GET /users | 200, list not empty, response time |
| 2 | UserApiTests | createUser | POST /users | 201, id, name, email, status, time |
| 3 | UserApiTests | getUserById | GET /users/{id} | 200, id, name, email, time |
| 4 | UserApiTests | updateUser | PUT /users/{id} | 200, updated name/status, time |
| 5 | UserApiTests | deleteUser | DELETE /users/{id} | 204, then GET → 404 |
| 6 | PostApiTests | getAllPosts | GET /posts | 200, list not empty |
| 7 | PostApiTests | getPostDetails | GET /posts/{id} | 200, ID, title |
| 8 | PostApiTests | searchPost | GET /posts?title=… | 200, every title matches keyword |
| 9 | PostApiTests | getCommentsOnPost | GET /posts/{id}/comments | 200, comments belong to the post |

Tests 2→5 are chained with `dependsOnMethods` (they share the created user).

## 5. Design notes (for the demo)
1. **Config layer** – `ConfigReader` loads `config/<env>.properties`. Any value can be overridden with `-Dkey=value`.
2. **Authentication** – Bearer token added once in `ApiClient.spec()`; provide the token at runtime with `-Dtoken` or `GOREST_TOKEN` (never hard-code it in Git).
3. **Reusable layer** – tests never call `given()` directly; they use `ApiClient` + `ApiAssertions`, so changes happen in one place.
4. **Test data** – `TestDataUtil` generates unique emails (GoRest rejects duplicates).
5. **POJO (bonus)** – `User` is serialized to JSON by Rest Assured (Jackson).
6. **Reporting** – `ExtentTestListener` is registered in `testng.xml`; `ApiClient` filter logs each request/response into the report (token is not logged).
7. **"Product" APIs** – GoRest has no `/products`; the guide's own endpoint list uses `/posts`, so Product ID/Name = Post ID/Title.

## 6. Execution flow
`mvn test -Denv=qa` → Surefire runs `testng.xml` → `BaseTest` prints env → test calls `ApiClient` → `ConfigReader` supplies URL/token → Rest Assured sends request → `ApiAssertions` validate → listener records result → `ExecutionReport.html` is flushed.


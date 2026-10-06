# Playwright Hybrid Framework (Java)

Web UI and API test automation on Playwright for Java, TestNG and Allure.

## Run

| Goal | Command |
|---|---|
| All tests (3 parallel forks) | `mvn test` |
| Smoke only | `mvn test -Dgroups=smoke` |
| Web or API only | `mvn test -Dgroups=web` / `-Dgroups=api` |
| Other browser, headed | `mvn test -Dbrowser=firefox -Dheadless=false` |
| Fewer/more forks | `mvn test -Dforks=1` |
| Other environment | `mvn test -Denv=staging` (add `staging.*` keys to `config.properties`) |

On first run on a new machine, Playwright downloads browsers automatically.

## Report

Results land in `target/allure-results`. With the [Allure CLI](https://allurereport.org/docs/install/) installed:

```
allure serve target/allure-results
```

Failed web tests also leave `artifacts/screenshots/<test>.png` and `artifacts/traces/<test>.zip`
(open with `npx playwright show-trace <zip>`).

## Layout

- `src/main/java/.../config` - `Config`: -D overrides, then env-prefixed keys, then plain keys
- `src/main/java/.../core` - `PlaywrightManager`: per-thread browser lifecycle and tracing
- `src/main/java/.../pages` - page objects
- `src/main/java/.../api` - `ApiClient`: Playwright request context plus JSON helpers
- `src/test/java/.../tests` - tests (`web`, `api`), base classes, `FailureListener`

## Parallelism

Playwright Java is not safe to share across threads in one JVM, so parallelism uses Maven Surefire
forks (one JVM per fork), distributed per test class.

## CI

`.github/workflows/tests.yml` runs on push, PR and manually (with group and browser inputs),
and uploads Allure results and failure artifacts.

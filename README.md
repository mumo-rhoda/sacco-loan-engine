# SACCO Loan Eligibility & Contribution Tracker API

A Spring Boot service that automates member savings tracking and loan eligibility
decisions for a Savings and Credit Co-operative (SACCO) — the financial institution
that millions of Kenyans rely on for savings and credit, and which, in a large number
of real SACCOs, is still run on spreadsheets.

## The problem

A SACCO's core promise is simple: members save together, and can borrow against
their savings. In practice that means every loan officer has to manually:

- check how long someone has been a member,
- total up their contributions from a ledger or spreadsheet,
- check whether they already have a loan outstanding,
- and calculate whether the requested amount is within the SACCO's multiplier
  of their savings (commonly 3x).

Doing this by hand across hundreds or thousands of members is slow, inconsistent
between officers, and leaves no clean audit trail of *why* a loan was approved
or rejected. This project turns that decision into a deterministic, testable,
auditable rule engine behind a REST API.

## What it does

- **Member registration** — onboards SACCO members with a generated member number.
- **Contribution tracking** — records savings deposits per member and computes
  running totals.
- **Loan eligibility engine** (`LoanEligibilityService`) — evaluates a loan
  application against four rules, in the order a credit committee would apply them:
  1. Minimum membership tenure (default: 3 months)
  2. Minimum total contributions (default: KES 5,000)
  3. No existing pending or approved loan
  4. Requested amount must not exceed *N*x total contributions (default: 3x)
- **Loan decisioning** — every application is persisted with its outcome and the
  exact reason, whether approved or rejected — the audit trail a spreadsheet
  can't give you.
- **Event-driven disbursement hook** — on approval, a `LoanApprovedEvent` is
  published and handled asynchronously, simulating where a real disbursement
  call (e.g. an M-Pesa B2C API) or Kafka event would plug in without coupling
  it to the request/response cycle.

## What this project was built to demonstrate

- Layered architecture (controller → service → repository) with a clear
  separation between orchestration (`LoanService`) and business rules
  (`LoanEligibilityService`), so the rules can be unit tested in complete
  isolation from Spring, HTTP, and the database.
- A small rule-chain pattern instead of one long `if` block — each rule
  returns a specific, human-readable rejection reason, which is what actually
  makes a lending decision defensible.
- Spring's `ApplicationEventPublisher` for decoupling a decision from its
  side effects, with `@Async` so the "notification" doesn't block the caller.
- Bean Validation (`jakarta.validation`) with a centralized
  `@RestControllerAdvice` so every error — validation, not-found, conflict,
  unexpected — returns a consistent JSON shape.
- Flyway-versioned schema migrations instead of `ddl-auto: update`, so the
  schema history is explicit and reviewable.
- A test pyramid in miniature: fast Mockito unit tests on the rule engine,
  plus a Spring Boot + MockMvc integration test exercising real HTTP,
  validation, and persistence.
- Config externalized via `application.yml` (the 3-month / KES 5,000 / 3x
  thresholds are not hardcoded — a SACCO board could change its lending
  policy without a code change).

## Tech stack

Java 17 · Spring Boot 3.3 · Spring Data JPA · Flyway · H2 (dev) / PostgreSQL (prod)
· springdoc-openapi (Swagger UI) · JUnit 5 · Mockito · Docker

## Running it locally

Requires JDK 17 and Maven.

```bash
mvn spring-boot:run
```

The app starts on `http://localhost:8080` with an in-memory H2 database
(auto-migrated by Flyway, no setup needed). Swagger UI is at
`http://localhost:8080/swagger-ui.html`.

### Running with PostgreSQL via Docker

```bash
docker-compose up --build
```

This starts Postgres and the app together, with the app running under the
`prod` Spring profile.

### Running the tests

```bash
mvn test
```

## Example usage

```bash
# Register a member
curl -X POST http://localhost:8080/api/members \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Achieng Otieno","phoneNumber":"+254712345678","nationalId":"12345678"}'

# Record a contribution
curl -X POST http://localhost:8080/api/members/1/contributions \
  -H "Content-Type: application/json" \
  -d '{"amount": 8000, "reference": "MPESA-QGK123"}'

# Apply for a loan
curl -X POST http://localhost:8080/api/loans/apply \
  -H "Content-Type: application/json" \
  -d '{"memberId": 1, "requestedAmount": 20000}'
```

A freshly registered member will be rejected on the tenure rule until 3
"months" have passed — in a demo, you can lower
`sacco.eligibility.minimum-membership-months` in `application.yml`, or set
a member's `joinedDate` further in the past directly via the H2 console at
`/h2-console` to see an approval.

## Possible extensions

These were deliberately left out to keep the project's core focus (the
eligibility engine) legible, but are natural next steps:

- Spring Security + JWT for member/officer authentication and role-based access
- Guarantor rules (co-signer requirements for larger loans)
- Loan repayment schedule generation and tracking
- Kafka instead of an in-process event listener, for a true microservice split
  between decisioning and disbursement
- Testcontainers-backed integration tests against real PostgreSQL

## License

MIT — built as a portfolio project by [Castriq Technologies](https://castriq.com).

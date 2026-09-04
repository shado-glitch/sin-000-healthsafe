# IngestionServiceApp

## Overview

Parses and cleans `wards-outdated.csv`, a messy legacy export of wards, wings, and specialist departments data, and is the
first stop in the HealthSafe pipeline. Independent Maven module, no parent pom.

Part of the [HealthSafe](../README.md) project.

REST: exposes the cleaned records for `ward-service` (`../ward-service`) to
consume — see [Integration contracts](../README.md#integration-contracts) in the
root README for the endpoint shape.

## Example: one row cleaned

Input (`wards-outdated.csv`, row 6):

```
w-05,east wing ,PAEDIATRICS,five
```

Expected shape after cleaning (exact field names are up to you — this illustrates
the *kind* of transform expected, not a fixed schema to match exactly):

```json
{
  "wardId": "W-05",
  "wing": "East Wing",
  "department": "Paediatrics",
  "bedsAvailable": null,
  "notes": "bedsAvailable was non-numeric ('five') — flagged for follow-up"
}
```

Note this row is also a near-duplicate of `W-05` two rows above it (same real ward,
different ID casing and field values) — deciding how to merge or flag duplicates
like this is part of the exercise.

## Known data issues

`wards-outdated.csv` is deliberately messy — cleaning it is the point of this service. Look
out for (and handle) at least:

- **Inconsistent casing** in IDs, names, and status/category values (`Active` /
  `active` / `ACTIVE`)
- **Padding** — leading/trailing spaces, and the occasional double space, inside
  fields
- **Duplicate records** for the same real-world entity, written with a different ID
  casing/format and/or slightly different field values
- **Inconsistent date formats** (`YYYY-MM-DD`, `MM/DD/YYYY`, `DD-MM-YYYY`, one- and
  two-digit months/days) and outright invalid dates
- **Missing / placeholder values** — blank fields, `N/A`, `n/a`, `TBD`, `unknown`,
  `-`, `NaN`
- **Invalid or non-numeric values** in numeric columns (negative counts, spelled-out
  numbers, unrealistic values)
- **Inconsistent boolean/flag representations** (`Y`/`N`, `yes`/`no`, `1`/`0`,
  `true`/`FALSE`)
- **Naming/spelling variants** for the same thing (e.g. regional spelling
  differences, synonyms)


## Duplicate handling
When a duplicate ward ID is found, the first record is retained and the duplicate is not added as a second Ward. Instead, a note is added to the existing ward:

```json
{
  "wardId": "W-05",
  "wing": "East Wing",
  "department": "Paediatrics",
  "bedsAvailable": 5,
  "notes": "DUPLICATE: another record with the same ward ID was found"
}

```

This allows the duplicate to be handled without silently ignoring the fact that it occurred.

## Project structure

```
ingestion-service/
├── pom.xml
└── src/main/
    ├── java/co/wethinkcode/healthsafe/IngestionServiceApp.java
    └── resources/wards-outdated.csv
```

## Build

```
mvn package
```

## Run

```
java -jar target/ingestion-service.jar
```

Listens on port 7030.

The service reads and cleans `wards-outdated.csv` and exposes the cleaned
records through `GET /wards`. It also exposes `GET /health` for a basic
health check.

## Test

Automated JUnit 5 tests have been added to verify the cleaning and duplicate-handling behaviour.

Run the tests with:
```
mvn test
```

The service can also be checked manually:

```
curl http://localhost:7030/health   # -> OK
```

Cleaned ward records can be viewed with:

```
curl http://localhost:7030/wards
```

To add real tests, add JUnit 5 + the Surefire plugin to `pom.xml`, put tests under
`src/test/java/co/wethinkcode/healthsafe/`, and run `mvn test`.

# Library Management System (Spring Boot REST API)

A backend-only project: manage books, members, and borrowing/returning —
including automatic overdue fine calculation. Built with Spring Boot,
Spring Data JPA, and MySQL.

## What it does

- **Books**: add, view, search (by title/author), update, delete
- **Members**: add, view, update, delete
- **Borrowing**: borrow a book (checks stock), return a book (calculates
  a fine of ₹10/day if late), view a member's borrow history, view all
  currently overdue books

## Project structure

```
lms/
  src/main/java/com/library/lms/
    entity/         -> Book, Member, BorrowRecord, BorrowStatus (the database tables)
    repository/      -> interfaces that talk to the database (Spring Data JPA)
    service/         -> business logic (the "rules" of the library)
    controller/      -> REST API endpoints (what the outside world calls)
    exception/       -> custom errors + a handler that turns them into clean JSON responses
    LmsApplication.java -> the class you run to start everything
  src/main/resources/
    application.properties -> database connection settings
  pom.xml            -> Maven dependencies
```

This is the standard **layered architecture**: Controller → Service →
Repository → Database. Each layer only talks to the one directly below
it. This is exactly the pattern almost every real Spring Boot job will
expect you to know, so it's worth understanding *why* each layer
exists, not just copying it:

- **Controller**: only handles HTTP — reading the request, calling the
  service, returning a response. No business logic here.
- **Service**: the actual rules (e.g. "you can't borrow a book with 0
  copies left", "fine = ₹10 × days late"). This is the layer you'd unit
  test.
- **Repository**: just data access. Spring Data JPA writes the SQL for
  you based on method names.

## Setup

1. **Create the database** (you already have MySQL installed). Open a
   MySQL client and run:
   ```sql
   CREATE DATABASE lms_db;
   ```
2. Open `src/main/resources/application.properties` and set your MySQL
   `username` and `password` to match your local setup.
3. Open the project in IntelliJ (or your IDE of choice) as a Maven
   project — it will download the dependencies from `pom.xml`
   automatically.
4. Run `LmsApplication.java`. You should see Spring Boot start up and
   log `Tomcat started on port 8080`. Hibernate will create the
   `books`, `members`, and `borrow_records` tables for you the first
   time it runs.

## Testing it (with Postman, or any REST client)

Try this sequence to see the whole flow:

1. `POST /api/books`
   ```json
   { "title": "The Alchemist", "author": "Paulo Coelho", "isbn": "9780061122415", "totalCopies": 3 }
   ```
2. `POST /api/members`
   ```json
   { "name": "Ravi Kumar", "email": "ravi@example.com", "phone": "9876543210" }
   ```
3. `POST /api/borrow?bookId=1&memberId=1` — borrows the book, returns a
   record with a `dueDate` 14 days from today.
4. `GET /api/books/1` — notice `availableCopies` dropped from 3 to 2.
5. `PUT /api/borrow/return/1` — returns it. If you test this after the
   due date (or manually edit `dueDate` in the database to the past),
   you'll see `fineAmount` calculated automatically.
6. `GET /api/borrow/overdue` — lists every book that's still out past
   its due date.

Full endpoint list is in each `*Controller.java` file as comments above
each method.

## Why these design choices (for your interview prep)

- **`@ManyToOne` on BorrowRecord**: a book can be borrowed many times
  by many members over its life, and a member can borrow many books —
  so `BorrowRecord` is the join table that captures *one borrowing
  event*, not a permanent link.
- **Fine calculated on return, not on a schedule**: keeps the logic
  simple — no need for a background job. This is a reasonable v1; a
  natural "what would you improve" answer in an interview is adding a
  scheduled job (`@Scheduled`) to flag overdue books daily.
- **`ddl-auto=update`**: convenient for learning, but mention in an
  interview that production systems use migration tools like Flyway or
  Liquibase instead, since `update` can behave unpredictably on schema
  changes.
- **No DTOs yet**: controllers return entities directly, which is fine
  for a learning project but not best practice (it exposes your
  database structure directly in the API). A great next step once
  you're comfortable: add DTO classes so the API and the database
  schema can change independently.

## Natural next steps (good "what I'd add next" talking points)

- Spring Security + JWT login (admin vs member roles)
- DTOs + a mapping layer (MapStruct or manual mappers)
- Pagination on `GET /api/books` and `GET /api/members`
- Unit tests for `BorrowService` (the fine-calculation logic is the
  easiest, most valuable thing to test)
- A `@Scheduled` job that emails members when a book becomes overdue

# AGENTS.md — Momentum

## What this is

Spring Boot 4.1 / Java 21 Telegram bot for task management. Single-module Maven project, no monorepo.

## Build & run

```bash
# compile
mvn compile

# run (requires PostgreSQL + Telegram bot token env var)
mvn spring-boot:run

# package
mvn package -DskipTests
```

## Environment prerequisites

- **Java 21**
- **PostgreSQL** running on `localhost:5432`, database `momentum`, user `momentum_user`
- **Env var:** `MOMENTUM_MERSEDEH_TELEGRAM_BOT_TOKEN` — required at runtime
- Database uses `ddl-auto=update` (schema managed by Hibernate, no migration tool)

## Tests

**None.** No test files exist and `pom.xml` declares no test dependencies (no JUnit, no Mockito). `src/test/java/` is empty.

## Architecture

```
src/main/java/org/momentum/
├── SpringBootApplication.java    ← entry point (@EnableJpaAuditing)
├── dto/                          ← projection DTOs used in JPQL constructors
├── enums/                        ← ConversationState (state machine)
├── models/                       ← JPA entities: Task, Category (BaseEntity superclass)
├── repos/                        ← Spring Data repositories with @Query methods
├── services/                     ← business logic layer
└── telegram/                     ← bot layer (all interaction logic)
    ├── MomentumBot.java          ← routes updates to dispatchers
    ├── ConversationManager.java  ← per-chat state (in-memory ConcurrentHashMap)
    ├── callback/                 ← inline keyboard callback handlers
    └── message/                  ← text command/message handlers
```

**Dispatch pattern:** `MomentumBot.consume()` → `CallbackDispatcher` or `MessageDispatcher` → iterate handler list → first `supports()` match handles it. Command handlers (`/start`) are routed via `CommandDispatcher`.

## Key conventions & gotchas

- **Timezone hardcoded to `Asia/Tehran`** in `TaskService.findTodaysTasks()`. Do not change without updating all call sites.
- **Soft deletes** — entities use `deleted = 0/1` field; queries filter `WHERE deleted = 0`.
- **JPQL constructor expressions** in repositories (`CategoryRepository`, `TaskRepository`) project directly into DTOs. If you add/change entity fields, update these queries.
- **Conversation state is not persisted.** Lost on restart. Managed by `ConversationManager` with `ConcurrentHashMap<Long, Conversation>`.
- `DailyReport.java` and `WeeklyReport.java` are entirely commented out; `MonthlyReport.java` is an empty shell.
- `src/main/java/org/momentum/my tasks.txt` is a personal notes file accidentally committed — not part of the codebase.

## Known bugs to be aware of

- `CategoryService.updateCategory()` and `TaskService.updateTask()` reassign the local variable instead of copying fields onto the fetched entity — the `repository.save()` saves a detached object.
- `EditTaskMessageHandler` uses `Integer.getInteger()` (system property lookup) instead of `Integer.valueOf()` for parsing estimated minutes.
- `"DAILY_SUMMARY"` callback is rendered on the main menu keyboard but has no registered handler.

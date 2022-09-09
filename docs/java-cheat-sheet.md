# Java Cheat Sheet

## Gradle

### Force tasks to execute

You can force Gradle to execute all tasks ignoring up-to-date checks using the `--rerun-tasks`
option:

```console
./gradlew test --rerun-tasks
```

### Suppress progress logging

To disable the progress logging we can set the environment variable TERM to the value dumb.

```console
TERM=dumb ./gradlew test
```
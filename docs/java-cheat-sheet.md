# Java Cheat Sheet

## Gradle

### Force tasks to execute

You can force Gradle to execute all tasks ignoring up-to-date checks using the `--rerun-tasks`
option:

```console
gradle test --rerun-tasks
```
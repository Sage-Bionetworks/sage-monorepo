# CLAUDE.md

`libs/qtl/` is a collection of Angular libraries for the QTL analysis explorer.

## Commands

```bash
nx test qtl-<lib-name>    # run unit tests for a lib
nx lint qtl-<lib-name>    # lint a lib
nx start qtl-storybook    # serve Storybook (port 4403)
```

## Storybook

For components that don't call our own backend APIs (third-party service calls are fine): add a `.stories.ts` file for new components; for changes to existing components, adjust the existing story if the new behavior fits naturally, or add a new story variant for sufficiently distinct behavior. When it's unclear whether a new story is warranted, ask the user. Follow the same pattern as existing stories in `libs/qtl/`.

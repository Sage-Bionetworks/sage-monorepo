# Linters and Formatters

## Overview

## Linters

| File type  | File extension  | Linter           |
|------------|-----------------|------------------|
| Dockerfile | `Dockerfile`    | [hadolint]       |
| HTML       | `*.html`        | [Webhint]        |
| Java       | `*.java`        | [Checkstyle]     |
| SCSS       | `*.scss`        | [VS Code (SCSS)] |
| TypeScript | `*.ts`          | [ESLint]         |
| XML        | `*.xml`         | -                |

### Webhint

Linter:

- Webhint - VS Code extension

Linter configuration:

- `.hintrc`

## Formatters

| File type  | File extension | Formatter            | Package type |
|------------|----------------|----------------------|--------------|
| Dockerfile | `Dockerfile`   | -                    |              |
| HTML       | `*.html`       | [Prettier]           |              |
| Java       | `*.java`       | [google-java-format] | npm          |
| SCSS       | `*.scss`       | [VS Code (SCSS)]     |              |
| TypeScript | `*.ts`         | [ESLint]             |              |
| XML        | `*.xml`        | [Prettier]           |              |

Formatter:

- Prettier - VS Code extension
- Prettier - Node.js package

Formatter configuration:

- `.prettierrc`
- `.prettierignore`

### google-java-format

- Package type: `npm`
- Provided by: `package.json`
- Binary location: `${workspaceRoot}/node_modules/.bin/google-java-format`
- Configuration file: None
- Used by:
  - `.vscode/settings.json`

<!-- Links -->

[Webhint]: https://marketplace.visualstudio.com/items?itemName=webhint.vscode-webhint
[Prettier]: https://prettier.io
[ESLint]: https://eslint.org
[Checkstyle]: https://checkstyle.sourceforge.io/
[Language Support for Java by Red Hat]: https://marketplace.visualstudio.com/items?itemName=redhat.java
[hadolint]: https://github.com/hadolint/hadolint
[VS Code (SCSS)]: https://code.visualstudio.com/docs/languages/css
[google-java-format]: https://github.com/google/google-java-format
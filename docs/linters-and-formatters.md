# Linters and Formatters

## Overview

| Language   | File extension | Linter           | Formatter                              |
|------------|----------------|------------------|----------------------------------------|
| Dockerfile | `Dockerfile`   | [hadolint]       | -                                      |
| HTML       | `*.html`       | [Webhint]        | [Prettier]                             |
| Java       | `*.java`       | [Checkstyle]     | [Language Support for Java by Red Hat] |
| SCSS       | `*.scss`       | [VS Code (SCSS)] | [VS Code (SCSS)]                       |
| TypeScript | `*.ts`         | [ESLint]         | [ESLint]                               |
| XML        | `*.xml`        | -                | [Prettier]                             |

## Linters

### Webhint

Linter:

- Webhint - VS Code extension

Linter configuration:

- `.hintrc`

## Formatters

Formatter:

- Prettier - VS Code extension
- Prettier - Node.js package

Formatter configuration:

- `.prettierrc`
- `.prettierignore`

<!-- Links -->

[Webhint]: https://marketplace.visualstudio.com/items?itemName=webhint.vscode-webhint
[Prettier]: https://prettier.io
[ESLint]: https://eslint.org
[Checkstyle]: https://checkstyle.sourceforge.io/
[Language Support for Java by Red Hat]: https://marketplace.visualstudio.com/items?itemName=redhat.java
[hadolint]: https://github.com/hadolint/hadolint
[VS Code (SCSS)]: https://code.visualstudio.com/docs/languages/css

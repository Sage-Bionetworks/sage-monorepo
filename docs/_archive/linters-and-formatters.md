# Linters and Formatters

## Overview

## Linters

| File type  | File extension | Linter           |
| ---------- | -------------- | ---------------- |
| Dockerfile | `Dockerfile`   | [hadolint]       |
| HTML       | `*.html`       | ESLint           |
| Java       | `*.java`       | [Checkstyle]     |
| SCSS       | `*.scss`       | [VS Code (SCSS)] |
| TypeScript | `*.ts`         | [ESLint]         |
| XML        | `*.xml`        | -                |

## Formatters

| File type  | File extension | Formatter            | Package type |
| ---------- | -------------- | -------------------- | ------------ |
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

## Auto Format Code On Save

### Java

- File type(s): `*.java`
- TODO

### TypeScript

- File type(s): `*.ts`
- On Save listener: [VS Code extension ESLint]
- Actions:
  1. The VS Code extension ESLint formats the file according to the project config defined in
     `.eslintrc.json`. The ESLint project config file references the ESLint workspace config file.
  2. The rules defined in Prettier workspace config `.prettierrc` are read from ESLint workspace
     config file `.eslintrc.json`.

Notes:

- The VS Code extension ESLint format files on save by default. This extension seems to ignore VS
  Code settings like `"editor.formatOnSave": false`. To turn off the auto formatting for this
  extension:
  ```json
  "editor.codeActionsOnSave": {
    // "source.fixAll": false,
    "source.fixAll.eslint": false
  },
  ```

<!-- Links -->

[prettier]: https://prettier.io
[eslint]: https://eslint.org
[checkstyle]: https://checkstyle.sourceforge.io/
[language support for java by red hat]: https://marketplace.visualstudio.com/items?itemName=redhat.java
[hadolint]: https://github.com/hadolint/hadolint
[vs code (scss)]: https://code.visualstudio.com/docs/languages/css
[google-java-format]: https://github.com/google/google-java-format
[VS Code extension Run on Save]: https://marketplace.visualstudio.com/items?itemName=emeraldwalk.RunOnSave
[VS Code extension ESLint]: https://marketplace.visualstudio.com/items?itemName=dbaeumer.vscode-eslint

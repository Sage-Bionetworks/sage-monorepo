# App Styles, Themes and Assets

## Overview

This document describes how SCSS styles, themes and assets are organized across
a web app and its libraries.

## Introduction

The style of an Angular component is defined in the file
`<component>.component.scss`. This file is specified in the Angular component
file `<component>.component.ts`. When building the app, Angular bundles the
individual component style files together into the file
`dist/apps/<app>/styles.<hash>.css`.

## File Organization

```console
apps/
└─ web-app/
   └─ src/
      ├─ _app-themes.scss
      ├─ index.html
      └─ styles.scss                    <---- imports web-app styles
dist/
└─ apps/
   └─ web-app/
      ├─ index.html
      └─ styles.<hash>.css              <---- compiled web-app styles
libs/
├─ shared/
│  ├─ assets/                           <---- cross-app assets
│  │  └─ src/
│  │     ├─ assets/
|  |     |  └─ images/
│  │     └─ favicon.ico
│  └─ styles/                           <---- cross-app styles
|     └─ src/
│        ├─ libs/
│        |  ├─ _constants.scss
│        |  └─ _general.scss
|        └─ index.scss
└─ web/
   ├─ assets/                           <---- web-app assets
   │  └─ src/
   │     ├─ assets/
   |     |  └─ images/
   │     └─ favicon.ico
   └─ styles/                           <---- web-app styles
      └─ src/
         ├─ libs/
         |  ├─ _constants.scss
         |  └─ _general.scss
         └─ index.scss
```

### App files

- The app style is defined in `apps/<app></app>/src/styles.scss`. This file is
  specified in the build option of the app in the app `project.json`. The main
  purpose of the app style file is to import the app style file (see below).
- The app theme is defined in `apps/<app></app>/src/_app-theme.scss`. This file is
  imported in by the app style file. The app theme file is responsible for
  referencing the theme files of all the Angular components used by the app.

### App-specific library files



### Library files shared across apps

### Draft

Additionally, the file starts with an underscore (`_`), indicating that this is a
Sass partial file. See the [Sass
documentation](https://sass-lang.com/guide#topic-4) for more information about
partial files.
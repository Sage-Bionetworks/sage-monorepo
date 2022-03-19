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

- The app style is defined in `apps/<app></app>/src/styles.scss`. This file is
  specified in the build option of the app in the app `project.json`. The main
  purpose of the app style file is to import the app style file (see below).
- The app theme is defined in `apps/<app></app>/src/_app-theme.scss`. This file is
  imported in by the app style file. The app theme file is responsible for
  referencing the theme files of all the Angular components used by the app.
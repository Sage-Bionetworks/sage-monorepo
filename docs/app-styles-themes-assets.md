# App Styles, Themes and Assets

## Overview

This document describes how SCSS styles, themes and assets are organized across
a web app and its libraries.

## File Structure

Each Angular component has its own style file (css, scss, less, etc). When
building the app, Angular includes the component style files into the app style
bundle. Additional styling files are used in our applications. These files are
organized as shown below.

```console
apps/
└─ web-app/
   └─ src/
      ├─ _app-themes.scss               <---- app styles (2)
      ├─ index.html
      └─ styles.scss                    <---- app styles (1)
dist/
└─ apps/
   └─ web-app/
      ├─ index.html
      └─ styles.<hash>.css              <---- app style bundle (7)
libs/
├─ shared/
│  ├─ assets/                           <---- cross-app assets
│  ├─ styles/                           <---- cross-app styles (5)
│  └─ themes/                           <---- cross-app assets (6)
└─ web/
   ├─ assets/                           <---- app-specific assets
   ├─ styles/                           <---- app-specific styles (3)
   └─ themes/                           <---- app-specific assets (4)
```

1. The app style - This file imports the app styles defined in the app library
   `libs/web/styles` and the app themes defined in
   `apps/web-app/src/_app-themes.scss`. This file is referenced in the build
   options defined in the `project.json` of the app.
2. The app theme - This file defines the theme configuration of the app (color
   and typography). This file imports the themes defined in the app library
   `libs/web/themes`.
3. The app styles library - This library defines the styles of the application.
   This library extends the cross-app styles defined in `libs/challenge-shared-web/styles`.
4. The app themes library - This library references the theme files of all the
   components defined in `libs/web`.
5. The cross-app style library - This library defines styles shared accross
   applications.
6. The cross-app theme library - This library defined themes shared across
   applications.
7. The app style bundle - This bundle includes the styles of the Angular
   components as well as the app style and theme files of the app.

Assets like images are used in HTML and SCSS files. Assets that are shared
across applications are stored in `libs/challenge-shared-web/assets`. Assets that are specific
to an app are located in `libs/<app>/assets` (e.g., where `libs/web/assets`). In
order to use these assets in the app and libraries of the `web-app` application,
the path to the asset folders are referenced in the build options of the app
(see `project.json`). This is also where a prefix is defined to distinguish the
asset files from these libraries. This example shows how to import an image from
the cross-app and app-specific asset libraries.

```html
<img src="/challenge-shared-web-assets/images/github.png">
<img src="/challenge-registry-assets/images/challenge-view-header-background.png">
```

## Applying a theme to a component

Create a file named `_<component>-theme.scss` that will be used to apply a theme
to a component. While the style file of a component is responsible for the
layout of the component, the theme file is responsible for defining the colors
and typography of the component.

```console
$ ls -1 libs/web/about/src/lib/
about.component.html
about.component.scss                   <---- style file
about.component.spec.ts
about.component.ts
about.module.ts
_about-theme.scss                      <---- theme file
```

Seed the theme file with the following template:

```scss
@use 'sass:map';
@use '@angular/material' as mat;

@mixin color($theme) {
  $config: mat.get-color-config($theme);
  $primary: map.get($config, primary);
  $accent: map.get($config, accent);
  $warn: map.get($config, warn);
  $background: map.get($config, background);
  $foreground: map.get($config, foreground);

  // Specify the colors of the elements of the component here.
  // Example:
  .awesome-class {
    color: mat.get-color-from-palette($primary, default);
  }
}

@mixin typography($theme) {
  $config: mat.get-typography-config($theme);

  // Specify the typography of the elements of the component here.
  // Example:
  .awesome-class {
    font-family: mat.font-family($config);
    font-size: mat.font-size($config);
    font-weight: mat.font-weight($config);
  }
}

@mixin theme($theme) {
  $color-config: mat.get-color-config($theme);
  @if $color-config != null {
    @include color($theme);
  }

  $typography-config: mat.get-typography-config($theme);
  @if $typography-config != null {
    @include typography($theme);
  }
}
```

Reference the theme file created in the index theme file `_lib-theme.scss` of
the library you are working on.

```scss
@use './lib/<component>-theme';                  <---- import theme file
@use './lib/<component-2>-theme';

@mixin theme($theme) {
  @include <component>-theme.theme($theme);      <---- reference theme file
  @include <component-2>-theme.theme($theme);
}
```

If you are creating a new library, you need to reference the library index theme
file in the index file of the `themes` library of the application. The `themes`
library of the application `web-app` is located in `libs/web/themes`. Open its
index file `libs/web/themes/src/_index.scss` and add the reference to the index
theme file of the new library.

```scss
@use 'libs/web/<lib>/src/lib-theme' as web-<lib>;
@use 'libs/web/<lib-2>/src/lib-theme' as web-<lib-2>;

@mixin theme($theme) {
  @include web-<lib>.theme($theme);
  @include web-<lib-2>.theme($theme);
}
```

The theme defined for the application should now be applied to the component.
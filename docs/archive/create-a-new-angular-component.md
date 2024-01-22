This doc describes how to create a new library + component in the OpenChallenges app, though
the steps can be applied to any app in this project. This doc will also include information on
where/how to copy-paste code from the [Figma-to-code export] into the app (starting at [Step 5]).

## 1. Create a new Angular library

To create a UI library within `openchallenges`, run:

```console
nx g @nx/angular:lib <new library name> --directory openchallenges
```

(Optional but recommended) Use `--dryrun` to first see what and where the entities will be created;
this will help visualize and validate the intended directory structure, e.g.

```console
$ nx g @nx/angular:lib awesome-lib --directory openchallenges --dry-run

>  NX  Generating @nx/angular:library

...
UPDATE workspace.json
CREATE libs/openchallenges/awesome-lib/README.md
CREATE libs/openchallenges/awesome-lib/tsconfig.lib.json
CREATE libs/openchallenges/awesome-lib/tsconfig.spec.json
CREATE libs/openchallenges/awesome-lib/src/index.ts
CREATE libs/openchallenges/awesome-lib/src/lib/openchallenges-awesome-lib.module.ts
CREATE libs/openchallenges/awesome-lib/tsconfig.json
CREATE libs/openchallenges/awesome-lib/project.json
UPDATE tsconfig.base.json
CREATE libs/openchallenges/awesome-lib/jest.config.ts
CREATE libs/openchallenges/awesome-lib/src/test-setup.ts
CREATE libs/openchallenges/awesome-lib/.eslintrc.json

NOTE: The "dryRun" flag means no changes were made.
```

Due to how the OpenChallenges app is currently structured, some additional
steps are required:

1.  Remove `openchallenges-` from the filename of the module TypeScript in `src/lib/`, e.g.

    `openchallenges-awesome-lib.module.ts` → `awesome-lib.module.ts`

2.  Simiarly, in `src/index.ts`, remove `openchallenges-` from the import filepath.
3.  In the library module (`<new library name>.module.ts`), remove `ChallengeRegistry` from
    the class name, e.g.
    `export class ChallengeRegistryAwesomeLibModule {}` → `export class AwesomeLibModule {}`

> **Note**: still have questions about libraries? See [Libraries] for more details.

## 2. Create a new Angular component

To create the component, use:

```console
nx g @nx/angular:component <new component name> --project <project-name>
```

where `<project name>` is the name of the project defined in `project.json` of the newly-created
Angular library.

For example, to create an Angular component for the `awesome-lib` library, the project name would be
`openchallenges-awesome-lib`, as defined in `libs/openchallenges/awesome-lib/project.json`:

```json
{
  "name": "openchallenges-awesome-lib",
  "$schema": "../../../node_modules/nx/schemas/project-schema.json",
  "projectType": "library",
  ...
}
```

The resulting command would then be:

```console
$ nx g @nx/angular:component awesome-lib --project openchallenges-awesome-lib --dry-run

>  NX  Generating @nx/angular:component

CREATE libs/openchallenges/awesome-lib/src/lib/awesome-lib/awesome-lib.component.scss
CREATE libs/openchallenges/awesome-lib/src/lib/awesome-lib/awesome-lib.component.html
CREATE libs/openchallenges/awesome-lib/src/lib/awesome-lib/awesome-lib.component.spec.ts
CREATE libs/openchallenges/awesome-lib/src/lib/awesome-lib/awesome-lib.component.ts
UPDATE libs/openchallenges/awesome-lib/src/lib/awesome-lib.module.ts

NOTE: The "dryRun" flag means no changes were made.
```

> **Note**: notice that the command above is using `dry-run`. Again, this is just to ensure
> that the new entities will be created in the right folder. If everything looks correct,
> remove the flag to actually create the new component.

To directly create the files into the parent folder, use `--flat` in the command:

```console
$ nx g @nx/angular:component awesome-lib --project openchallenges-awesome-lib --flat -dry-run

>  NX  Generating @nx/angular:component

CREATE libs/openchallenges/awesome-lib/src/lib/awesome-lib.component.scss
CREATE libs/openchallenges/awesome-lib/src/lib/awesome-lib.component.html
CREATE libs/openchallenges/awesome-lib/src/lib/awesome-lib.component.spec.ts
CREATE libs/openchallenges/awesome-lib/src/lib/awesome-lib.component.ts
UPDATE libs/openchallenges/awesome-lib/src/lib/awesome-lib.module.ts

NOTE: The "dryRun" flag means no changes were made.
```

Notice how the component files would be created directly in `awesome-lib/src/lib/`, compared
with `awesome-lib/src/lib/awesome-lib/` when `--flat` is _not_ used.

Before moving on, some additional edits are required:

1.  Remove the `<new component name>.component.spec.ts` file -- it is not needed.
2.  In the `<new component name>.component.ts` file:

    - Remove `OnInit` from the import and all instances of it from the class
    - Import `ConfigService` from `@sagebionetworks/openchallenges/config`
    - Pass `configService` into the constructor as `private readonly`

    The final result should look something like this:

    ```ts
    import { Component } from '@angular/core';
    import { ConfigService } from '@sagebionetworks/openchallenges/config';

    @Component({
      selector: 'sagebionetworks-awesome-lib',
      templateUrl: './awesome-lib.component.html',
      styleUrls: ['./awesome-lib.component.scss'],
    })
    export class AwesomeLibComponent {
      constructor(private readonly configService: ConfigService) {}
    }
    ```

3.  Revisit the library module (`<library name>.module.ts`) and import the UI and
    routing modules:

        ```ts
        ...
        import { RouterModule, Routes } from '@angular/router';

        const routes: Routes = [{ path: '', component: <component> }];

        @NgModule({
          imports: [CommonModule, RouterModule.forChild(routes)],
          ...
        ```

        where `<component>` is the newly-created Angular component. You will also need
        to export the newly-created Angular component, e.g.

        ```ts
        @NgModule({
          ...
          exports: [AwesomeLibComponent],
        })
        ```

        The final result should look something like this:

          ```ts
          import { NgModule } from '@angular/core';
          import { CommonModule } from '@angular/common';
          import { RouterModule, Routes } from '@angular/router';
          import { AwesomeLibComponent } from './awesome-lib.component';

          const routes: Routes = [{ path: '', component: AwesomeLibComponent }];

          @NgModule({
            imports: [CommonModule, RouterModule.forChild(routes)],
            declarations: [AwesomeLibComponent],
            exports: [AwesomeLibComponent],
          })
          export class AwesomeLibModule {}
          ```

## 3. Add routing

In `apps/openchallenges/src/app/app-routing.module.ts`, add a router for the new component, e.g.

```ts
  {
    path: <new path name>,
    loadChildren: () =>
      import('<index>').then(
        (m) => m.<module>
      ),
  },
```

where `<index>` is the path defined `tsconfig.base.json`. For example, the base for AwesomeLib is:

```json
{
  ...
  "paths": {
    "@sagebionetworks/openchallenges/awesome-lib": [
      "libs/openchallenges/awesome-lib/src/index.ts"
    ],
    ...
  }
}
```

So, the resulting router would look something like this:

```ts
  {
    path: 'awesome-path',
    loadChildren: () =>
      import('@sagebionetworks/openchallenges/awesome-lib').then(
        (m) => m.AwesomeLibModule
      ),
  },
```

> ⚠️ **IMPORTANT**: we follow [Angular's standards for routing order], that is:
>
> _List routes with a static path first, followed by an empty path route, which matches the default route. The wildcard route comes last because it matches every URL and the Router selects it only if no other routes match first._
>
> Please adhere to this recommended order when adding a new route.

## 4. Time to test! ☕

If you haven't already, start a local server to test the newly-created component:

```
nx serve openchallenges
```

If everything is setup correctly, `http://localhost:4200/<new path name>` will open a web page that
displays something like this:

```
<component name> works!
```

## 5. Import code from the Figma-to-Code export

### Angular export

If the Figma-to-code export was downloaded as Angular, the directory structure should be comparative
to our current app structure, that is:

```
<project name>-angular
├── angular.json
├── browserslist
├── package.json
├── src
│   ├── app
│   │   ├── app.component.css
│   │   ├── app.component.html
│   │   ├── app.component.ts
│   │   ├── app.module.ts
│   │   ├── components
│   │   │   └── components.module.ts
│   │   └── pages
│   │       ├── *
│   │       │   ├── *.component.css
│   │       │   ├── *.component.html
│   │       │   ├── *.component.ts
│   │       │   └── *.module.ts
│   │       └── **
│   │           ├── **.component.css
│   │           ├── **.component.html
│   │           ├── **.component.ts
│   │           └── **.module.ts
│   ├── environments
│   │   ├── environment.prod.ts
│   │   └── environment.ts
│   ├── index.html
│   ├── main.ts
│   ├── polyfills.ts
│   └── styles.css
├── tsconfig.app.json
├── tsconfig.json
└── tslint.json
```

Locate the HTML and CSS files within `src/app/pages/`, then copy-paste the code as needed.
Alternatively, you can move the files into the app, then re-direct the paths in `*.component.ts`
so that it uses these files:

```ts
@Component({
  selector: 'openchallenges-team',
  templateUrl: './<new HTML file>',
  styleUrls: ['./<new CSS/SCSS file>'],
})
```

### HTML export

If the Figma-to-code export was downloaded as HTML, the directory structure will be very simple:

```
<project name>-html
├── *.css
├── *.html
├── **.css
├── **.html
├── package.json
└── style.css
```

Copy-paste the HTML and CSS as needed, or move the files into the app then re-direct the paths
in `*.component.ts`.

## 6. Update themes

Once the exported styles files are copy-pasted to the app, some additional steps are required to configurate the themes of exported pages. Take awesome-lib as an example:

```
├── awesome-lib
│   ├── src
│   │   ├── lib
│   │   │   ├── awesome-lib.component.css
│   │   │   ├── awesome-lib.component.html
│   │   │   ├── awesome-lib.component.ts
│   │   │   ├── awesome-lib.module.ts
│   │   │   └── sub-awesome-lib
│   │   │       ├── sub-awesome-lib.component.css
│   │   │       ├── sub-awesome-lib.component.html
│   │   │       ├── sub-awesome-lib.component.ts
│   │   │       └── sub-awesome-lib.module.ts
...
```

1. Create theme files `_<component-name>-theme.scss` in each component, i.e. `libs/openchallenges/awesome-lib/_awesome-lib-theme.scss` and `libs/openchallenges/awesome-lib/src/lib/sub-awesome-lib/_sub-awesome-lib-theme.css` (repeat the step for each sub-component if any). Copy-paste below essential codes into all `_<component-name>-theme.scss` files:

   ```scss
   @use 'sass:map';
   @use '@angular/material' as mat;

   @mixin color($theme) {
     $config: mat.get-color-config($theme);
     $primary: map.get($config, 'primary');
     $accent: map.get($config, 'accent');
     $warn: map.get($config, 'warn');
     $figma: map.get($config, 'figma');
   }

   @mixin typography($theme) {
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

2. Create `awesome-lib/src/_lib_themes.scss` and import all the themes of components:

   ```scss
   @use './lib/awesome-lib-theme' as awesome-lib;
   @use './lib/sub-awesome-lib/sub-awesome-lib-theme' as sub-awesome-lib;
   @mixin theme($theme) {
     @include awesome-lib.theme($theme);
     @include sub-awesome-lib.theme($theme);
   }
   ```

3. Load the themes of awesome-lib library in `libs/openchallenges/themes/src/_index.scss` with below snippet:

   ```scss
   @use 'libs/openchallenges/awesome-lib/src/lib-theme' as openchallenges-awesome-lib;
   @mixin theme($theme) {
     @include openchallenges-awesome-lib.theme($theme);
   }
   ```

   At this point, all theme files have been generated:

   ```
   ├── awesome-lib
   │   ├── src
   │   │   ├── _lib-theme.scss
   │   │   ├── lib
   │   │   │   ├── _awesome-lib-theme.scss
   │   │   │   ├── awesome-lib.component.css
   │   │   │   ├── awesome-lib.component.html
   │   │   │   ├── awesome-lib.component.ts
   │   │   │   ├── awesome-lib.module.ts
   │   │   │   └── sub-awesome-lib
   │   │   │       ├── _sub-awesome-lib-theme.css
   │   │   │       ├── sub-awesome-lib.component.css
   │   │   │       ├── sub-awesome-lib.component.html
   │   │   │       ├── sub-awesome-lib.component.ts
   │   │   │       └── sub-awesome-lib.module.ts
   ...
   ```

4. In each component including sub-components, move all the colors and fonts from `<component-name>.component.scss` to `_<component-name>-themes.scss`.

5. Now, the themes of new exported page has been configurated. However, some manually fine-tunes will still require to make page look like what figma's design. It's recommended to adjust the styles with the app's local server

   ```
   nx serve openchallenges
   ```

Most palettes used in the figma has been already configured in the app and defined in `libs/themes/src/_palettes.scss`. The palettes can be always retrieved via `map.get(<theme-object-name>, <color-variable-name>)`, i.e. `map.get($figma, dl-color-default-hover1)`. It's similar with the constant variables defined in `libs/styles/src/lib/_constants.scss`, i.e. `border-radius: constants.$dl-radius-radius-radius16;`

> **Note**<br>All color/font styles needs to be defined in `_<component-name>-theme.scss`.

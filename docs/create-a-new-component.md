# New App Component

## Overview

This doc will describe how to create a new library + component in the Challenge Registry app, though
the steps can be applied to any app in this project.  This doc will also include information on
where/how to copy-paste code from the [Figma-to-code export] into the app (starting at [Step 5]).

## 1. Create a new Angular library

To create a UI library within `challenge-registry`, run:

```console
nx g @nrwl/angular:lib <new library name> --directory challenge-registry
```
 
(Optional but recommended) Use `--dryrun` to first see what and where the entities will be created;
this will help visualize and validate the intended directory structure, e.g.

```console
$ nx g @nrwl/angular:lib awesome-lib --directory challenge-registry --dry-run

>  NX  Generating @nrwl/angular:library

...
UPDATE workspace.json
CREATE libs/challenge-registry/awesome-lib/README.md
CREATE libs/challenge-registry/awesome-lib/tsconfig.lib.json
CREATE libs/challenge-registry/awesome-lib/tsconfig.spec.json
CREATE libs/challenge-registry/awesome-lib/src/index.ts
CREATE libs/challenge-registry/awesome-lib/src/lib/challenge-registry-awesome-lib.module.ts
CREATE libs/challenge-registry/awesome-lib/tsconfig.json
CREATE libs/challenge-registry/awesome-lib/project.json
UPDATE tsconfig.base.json
CREATE libs/challenge-registry/awesome-lib/jest.config.ts
CREATE libs/challenge-registry/awesome-lib/src/test-setup.ts
CREATE libs/challenge-registry/awesome-lib/.eslintrc.json

NOTE: The "dryRun" flag means no changes were made.
```

Due to how the Challenge Registry app is currently structured, some additional
steps are required:

1. Discard/undo changes made to `project.json` in other existing folders outside of the new library
folder, e.g. `apps/challenge-api-gateway/project.json`. There should be ~12 files left after
discarding those updates.
2. Remove `challenge-registry-` from the filename of the module TypeScript in `src/lib/`, e.g.
    
      `challenge-registry-awesome-lib.module.ts` → `awesome-lib.module.ts`

3. Simiarly, in `src/index.ts`, remove `challenge-registry-` from the import filepath.
4. In the library module (`<new library name>.module.ts`), remove `ChallengeRegistry` from 
the class name, e.g.
    
      `export class ChallengeRegistryAwesomeLibModule {}` → `export class AwesomeLibModule {}`
    
5. While in the library module, import the UI and routing modules:

    ```ts
    ...
    import { UiModule } from '@sagebionetworks/challenge-registry/ui';
    import { RouterModule, Routes } from '@angular/router';
    
    const routes: Routes = [{ path: '', component: <component> }];
    
    @NgModule({
      imports: [CommonModule, RouterModule.forChild(routes), UiModule],
      ...
    ```
    
    where `<component>` will be created in the next step.

> **Note**: still have questions about libraries?  See [Libraries] for more details.

## 2. Create a new Angular component

To create the component, use:

```console
nx g @nrwl/angular:component <new component name> --project <project-name>
```

where `<project name>` is the name defined in `workspace.json`.

For example, to create an Angular component for the `awesome-lib` library (located at
`libs/challenge-registry/awesome-lib`), the project name would be `challenge-registry-awesome-lib`, 
as defined in `workspace.json`:

```json
{
  ...
  "projects": {
    ...
    "challenge-registry-awesome-lib": "libs/challenge-registry/awesome-lib",
    ...
  }
}
```

The resulting command would then be:

```console
$ nx g @nrwl/angular:component awesome-lib --project challenge-registry-awesome-lib --dry-run   

>  NX  Generating @nrwl/angular:component

CREATE libs/challenge-registry/awesome-lib/src/lib/awesome-lib/awesome-lib.component.scss
CREATE libs/challenge-registry/awesome-lib/src/lib/awesome-lib/awesome-lib.component.html
CREATE libs/challenge-registry/awesome-lib/src/lib/awesome-lib/awesome-lib.component.spec.ts
CREATE libs/challenge-registry/awesome-lib/src/lib/awesome-lib/awesome-lib.component.ts
UPDATE libs/challenge-registry/awesome-lib/src/lib/awesome-lib.module.ts

NOTE: The "dryRun" flag means no changes were made.
```

> **Note**: notice that the command above is using `dry-run`.  Again, this is just to ensure
> that the new entities will be created in the right folder.  If everything looks correct,
> remove the flag to actually create the new component.

To directly create the files into the parent folder, use `--flat` in the command:

```console
$ nx g @nrwl/angular:component awesome-lib --project challenge-registry-awesome-lib --flat -dry-run

>  NX  Generating @nrwl/angular:component

CREATE libs/challenge-registry/awesome-lib/src/lib/awesome-lib.component.scss
CREATE libs/challenge-registry/awesome-lib/src/lib/awesome-lib.component.html
CREATE libs/challenge-registry/awesome-lib/src/lib/awesome-lib.component.spec.ts
CREATE libs/challenge-registry/awesome-lib/src/lib/awesome-lib.component.ts
UPDATE libs/challenge-registry/awesome-lib/src/lib/awesome-lib.module.ts

NOTE: The "dryRun" flag means no changes were made.
```

Notice how the component files would be created directly in `awesome-lib/src/lib/`, compared
with `awesome-lib/src/lib/awesome-lib/` when `--flat` is _not_ used.

Before moving on, some additional edits are required:

1. Remove the `<new component name>.component.spec.ts` file -- it is not needed.
2. In the `<new component name>.component.ts` file:
    * Remove `OnInit` from the import and all instances of it from the class
    * Import `ConfigService` from `@sagebionetworks/challenge-registry/config`
    * Pass `configService` into the constructor as `private readonly`

    The final result should look something like this:
    
      ```ts
      import { Component } from '@angular/core';
      import { ConfigService } from '@sagebionetworks/challenge-registry/config';

      @Component({
        selector: 'sagebionetworks-awesome-lib',
        templateUrl: './awesome-lib.component.html',
        styleUrls: ['./awesome-lib.component.scss'],
      })
      export class AwesomeLibComponent {
        constructor(private readonly configService: ConfigService) {}
      }
      ```
   
3. Revisit the library module (`<library name>.module.ts`) and replace `<component>` 
   with the newly-created component, e.g.

    ```ts
    const routes: Routes = [{ path: '', component: AwesomeLibComponent }];
    ```

    Additionally, export the newly-created Angular component, e.g.
    
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
      import { UiModule } from '@sagebionetworks/challenge-registry/ui';
      import { RouterModule, Routes } from '@angular/router';
      import { AwesomeLibComponent } from './awesome-lib.component';

      const routes: Routes = [{ path: '', component: AwesomeLibComponent }];

      @NgModule({
        imports: [CommonModule, RouterModule.forChild(routes), UiModule],
        declarations: [AwesomeLibComponent],
        exports: [AwesomeLibComponent],
      })
      export class AwesomeLibModule {}
      ```

## 3. Add routing

In `apps/challenge-registry/src/app/app-routing.module.ts`, add a router for the new component, e.g.

```ts
  {
    path: <new path name>,
    loadChildren: () =>
      import('<index>').then(
        (m) => m.<module>
      ),
  },
```

where `<index>` is the path defined `tsconfig.base.json`.  For example, the base for AwesomeLib is:

```json
{
  ...
  "paths": {
    "@sagebionetworks/challenge-registry/awesome-lib": [
      "libs/challenge-registry/awesome-lib/src/index.ts"
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
      import('@sagebionetworks/challenge-registry/awesome-lib').then(
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
$ challenge-registry-serve
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
  selector: 'challenge-registry-team',
  templateUrl: './<new HTML file>',
  styleUrls: ['./<new CSS/SCSS file>'],
})
```

### Angular export

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

## 6. Add themes

- User-Profile:

  1. Create `user-profile/src/lib/_user-profile-themes.scss`.
  2. Move all the colors and fonts from `user-profile.component.scss` to `_user-profile-themes.scss`.
  3. Create `user-profile/src/_lib_themes.scss` and load `_user-profile-themes`.
     ```scss
     @use './lib/user-profile-theme' as user-profile;
     @mixin theme($theme) {
       @include user-profile.theme($theme);
     }
     ```
  4. Load themes of user-profile in `libs/challenge-registry/themes/src/_index.scss`.
     ```scss
     @use 'libs/challenge-registry/user-profile/src/lib-theme' as challenge-registry-user-profile;
     @mixin theme($theme) {
       @include challenge-registry-user-profile.theme($theme);
     }
     ```

- Sub-components (repeated above step 1-3 for each sub component): `_user-profile-[overview|challenges|starred|stats]-theme.scss`.

## 7. Update styles

- User-Profile

  1. The layout of starred section was different from challenges/biography, which caused the starred section to be placed beyong section body box. Discuss with Verena what could be the source of the issue. To fix, use the same layout on the starred section.
  2. Change background color of tab content from "grey" to transparent, in order to match with what color shown in figma:
     ```scss
     .base-main-section {
       background-color: transparent;
     }
     ```
  3. Add hover colors to on all the tab switch buttons, since it was only applied to the "biography" button:
     ```scss
     .base-bio,
     .base-challenges,
     .base-stars {
       &:focus,
       &:hover,
       &:active {
         background-color: $dl-color-default-hover2;
       }
     }
     ```

- User-Profile-Stats

  1.  Remove the `top` property in the `.basic-stats-logged-in` and `.basic-stats-public` to correct the position of the box in the page.
      ```scss
      // remove below properties
      .basic-stats-logged-in {
        top: -141px;
      }
      .basic-stats-public {
        top: -1px;
      }
      ```
  2.  Change the "Edit Profile" from `div` to `button` in `.html` and add the "pointer" cursor in `.scss`

- User-Profile-Overview

  1. Add back class "main-section-bio-text1" to fix the positions between the Headline ("Biography", "Organizations") and contents (bio text, org card):

     ```html
     <span class="main-section-bio-text1">{{user.bio}}</span>
     ```

  2. Use `<span>` and for `{{user.bio}}`
  3. Since we already `org-card` component, move exported codes from `user-profile/src/lib/components/organization-card.[html|scss]` to `ui/src/lib/org-card/org-card.[html|scss]`
  4. Remove `<link href="./organization-card.css" rel="stylesheet" />` in `ui/src/lib/org-card/org-card.html`
  5. Rename `org-card` component to `organization-card` - [here](https://github.com/Sage-Bionetworks/challenge-registry/pull/434/commits/6945153) are relevant changes
  6. Create `_organization-card-theme.scss` and collects the colors/typography styles codes. Load `organization-card-theme` in `ui/src/_lib-theme.scss`
  7. Update the `organization-card.html` to use the mocked organization object - [here](https://github.com/Sage-Bionetworks/challenge-registry/pull/434/commits/da3480e) are relevant changes
  8. Further update the organization avatar to make it like in the figma:
     - Fix: `background-color`, `box-shadow` and `border-radius` is missing for `.organization-card-card-banner`
     - Since `<hallenge-registry-avatar>` is used to replace the exported codes for organization banner. Adjust background and font color to match the figma design:
       ```scss
       // add below properties in _organization-card-theme.scss
       .avatar-content {
         background-color: transparent !important;
         color: $dl-color-default-primary2 !important;
       }
       ```

- User-Profile-Challenges

  1. Create a `challenge-card` component (`nx g @nrwl/angular:component challenge-card --project=challenge-registry-ui`) using exported html/scss codes from `user-profile/src/lib/components/challenge-card.[html|scss]`
  2. Create a `challenge-card` module (`nx g @nrwl/angular:module challenge-card --project=challenge-registry-ui`) and ensure it can be used in `user-profile-challenges` - [here](https://github.com/Sage-Bionetworks/challenge-registry/pull/434/commits/ba19d8f) are relevant changes
  3. Remove `<link href="./challenge-card.css" rel="stylesheet" />` in `ui/src/lib/challenge-card/challenge-card.html`
  4. Create `_challenge-card-theme.scss` and collects the colors/typography styles codes. Load `challenge-card-theme` in `ui/src/_lib-theme.scss`
  5. Update the `challenge-card.html` to use the mocked challenge object - [here](https://github.com/Sage-Bionetworks/challenge-registry/pull/434/commits/85e6d8d) are relevant changes
  6. Similiar "bugs"/problems as mentoned in `organization-card`. Some styles only presented in the `media`, but it should be set as default. An easy fix for now is to comment out @media like below, so that the styles could be assigned.

     ```scss
     // @media (max-width: 767px) {
     .challenge-card-container1 {
       border-radius: $dl-radius-radius-radius16;
     }
     .challenge-card-starred {
       border-width: 1px;
       border-radius: $dl-radius-radius-radius16;
     }
     .challenge-card-card-footer {
       top: 440px;
       left: 0px;
       right: 0px;
       width: 451px;
       bottom: 20px;
       height: 71px;
       margin: auto;
     }
     .challenge-card-status-tag {
       top: 19px;
       right: 0px;
     }
     .challenge-card-difficulty-tag {
       top: 28px;
       left: 0px;
     }
     // }
     ```

  7. Fix the missing properties for status tag from exported codes:
     ```scss
     .challenge-card-status-tag {
       border-color: 1px solid $dl-color-default-primary2;
       background-color: $dl-color-default-hover1;
       color: #000;
     }
     ```
  8. Replace status image with css codes and fix layout inside of status box

- User-Profile-Starred

  1. Update contents for each tabs followed by <challenge-registry-challenge-card> and <challenge-registry-organization-card> - [here](https://github.com/Sage-Bionetworks/challenge-registry/pull/434/commits/9bca88f) is the relevant changes
  2. Fix some minor styles on the background and position of content container
  3. Remove border of `.main-section-stars-tab`
  4. Merge button container and text into one and fix hover colors - [here](https://github.com/Sage-Bionetworks/challenge-registry/pull/434/commits/b8d961b) is the relevant changes

- Replace exported codes of icons with matieral icon - [here](https://github.com/Sage-Bionetworks/challenge-registry/pull/434/commits/886200c) is an example to update the star icon on challenge card (font-weight is unnesscary for icon and removed in other commits):

  1. Use <mat-icon> with the corresponding icon name from [google fonts](https://fonts.google.com/icons).
  2. Add the same class name that was exported for the icon
  3. Update the styles of icon accordingly
  4. If the icon is before/after texts, some adjustments on dimensions might be also needed to make the icon align properly with texts - [here](https://github.com/Sage-Bionetworks/challenge-registry/pull/434/commits/8abcc7a) is an example.

## TO-DOs:

- Fix missing border/box-shadow of challenge/organization card, i.e the border color/box-shadow of `.challenge-card-container1` (I don't know how to get the information from figma)

## Issues found on the user profile component:

- Ensure Layouts among similar sections (position, height, weight) are consistent.

- Font weights have invalid format - with "px" after the value:

- Some styles is not found from the exported codes, i.e for the background color of organization banner, I do notice later it's added to the `media`, but missing in the default styles. Not sure if it's a bug from teleportHQ.

- Most of components are using `absolute` position and prefined height/weights.
  1. In the case, the components will not be reponsive enough. Take the biography tab content as example. Both "Biography" and "Organizations" has the same prefined height. If the content of "Biography" is more than the prefined size can handle, the text will be overlapped with the "Organizations" content. If the content of "Biography" only has a few words, it is a little better but will leave a lot of empty vertical space above "Organizations" headline.

[Figma-to-code export]: https://github.com/Sage-Bionetworks/challenge-registry/blob/main/docs/figma-to-code.md
[Libraries]: https://github.com/Sage-Bionetworks/challenge-registry/blob/main/docs/libraries.md
[Step 5]: #5-import-code-from-the-figma-to-code-export
[Angular's standards for routing order]: https://angular.io/guide/router#route-order

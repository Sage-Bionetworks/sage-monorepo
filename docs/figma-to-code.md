# Figma to Code

## User Profile

In `user-profile.component.html`:

- Update the `<div class="base-container">` to import the `user` object.
    ```html
    <div class="base-container"  *ngIf="user$ | async as user">
    ```
- Add the conditional section content to enable tab switcher
    ```html
    <section id="main" class="base-main-section">
        <ng-container [ngSwitch]="activeTab">
        <challenge-registry-user-profile-overview *ngSwitchCase="tabs['overview']" [user]="user"
            [orgs]="orgs"></challenge-registry-user-profile-overview>
        <challenge-registry-user-profile-challenges *ngSwitchCase="tabs['challenges']"
            [userId]="user.id"></challenge-registry-user-profile-challenges>
        <challenge-registry-user-profile-starred *ngSwitchCase="tabs['starred']" [user]="user" [loggedIn]="loggedIn">
        </challenge-registry-user-profile-starred>
        </ng-container>
    </section>
    ```
- Add `[routerLink]="." [routerParams]=...` to the `<a>` elements of the menu.
    ```html
    <div id="sidenav" class="base-side-nav">
      <div class="base-bio">
        <a class="base-text07" routerLink="." [queryParams]="{tab: 'overview'}">Biography</a>
      </div>
      <div class="base-challenges">
        <a class="base-text08" routerLink="." [queryParams]="{tab: 'challenges'}">Challenges</a>
      </div>
      <div class="base-stars">
        <a class="base-text09" routerLink="." [queryParams]="{tab: 'starred'}">Starred</a>
      </div>
    </div>
    ```
- Enable user's avatar
    1. Use `challenge-registry-avatar` component for `base-profile-pic` class:
        ```html
        <challenge-registry-avatar  class="base-profile-pic" [avatar]="userAvatar"></challenge-registry-avatar>
        ```

    2. Create avatar variable based on the user object
        ```typescript
        this.user$.subscribe(
          (user) =>
            (this.userAvatar = {
            name: user.name
              ? (user.name as string)
              : user.login.replace(/-/g, ' '),
            src: user.avatarUrl ? user.avatarUrl : '',
            size: 320,
            })
          );
        ``` 
    3. Remove dimensions in scss for avatar since it's defined in .ts:
        ```scss
        // remove below properties
        .base-profile-pic {
          width: 320px;
          height: 320px;
        }
        ```
       
## Create user-profile-stats component

 1. Create a new component `user-profile-stats` by running:
    ```console
    nx g @nrwl/angular:component user-profile-stats  --project=challenge-registry-user-profile
    ```
 2. Move codes from `basic-stats.html` and `basic-stats.scss` to the `user-profile-stats` component.
 3. Add `loggedIn` variable to determine whether to use the `public` or `loggin` box.
    ```html
    <challenge-registry-user-profile-stats class="base-basic-stats" [loggedIn]="loggedIn"></challenge-registry-user-profile-stats>
    ```

 ## Add themes:

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

## Update styles:
 - User-Profile
    1. The layout of starred section is different from challenges/biography, which caused the starred section to be placed beyong section body box. Discuss with Verena what could be the source of the issue. To fix, use the same layout on the starred section.
-  User-Profile-Stats
   1. Remove the `top` property in the `.basic-stats-logged-in` and `.basic-stats-public` to correct the position of the box in the page.
        ```scss
        // remove below properties
        .basic-stats-logged-in {
            top: -141px;
        }
        .basic-stats-public {
            top: -1px;
            }
        ```
    


 ## Issues:

1. Ensure Layouts among similar sections (position, height, weight) are consistent.

2. Font weights have invalid format - with "px" after the value:

2. The css is from teleportHQ is not reponsive enough.


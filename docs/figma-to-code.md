# Figma to Code

## User Profile

In `user-profile.component.html`:

- Update the `<div class="base-container">` to import the `user` object.
  ```html
  <div class="base-container" *ngIf="user$ | async as user"></div>
  ```
- Add the conditional section content to enable tab switcher
  ```html
  <section id="main" class="base-main-section">
    <ng-container [ngSwitch]="activeTab">
      <challenge-registry-user-profile-overview
        *ngSwitchCase="tabs['overview']"
        [user]="user"
        [orgs]="orgs"
      ></challenge-registry-user-profile-overview>
      <challenge-registry-user-profile-challenges
        *ngSwitchCase="tabs['challenges']"
        [userId]="user.id"
      ></challenge-registry-user-profile-challenges>
      <challenge-registry-user-profile-starred
        *ngSwitchCase="tabs['starred']"
        [user]="user"
        [loggedIn]="loggedIn"
      >
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
     <challenge-registry-avatar
       class="base-profile-pic"
       [avatar]="userAvatar"
     ></challenge-registry-avatar>
     ```

  2. Create avatar variable based on the user object
     ```typescript
     this.user$.subscribe(
       (user) =>
         (this.userAvatar = {
           name: user.name ? (user.name as string) : user.login.replace(/-/g, ' '),
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

1.  Create a new component `user-profile-stats` by running:
    ```console
    nx g @nrwl/angular:component user-profile-stats  --project=challenge-registry-user-profile
    ```
2.  Move codes from `basic-stats.html` and `basic-stats.scss` to the `user-profile-stats` component.
3.  Add `loggedIn` variable to determine whether to use the `public` or `loggin` box.
    ```html
    <challenge-registry-user-profile-stats
      class="base-basic-stats"
      [loggedIn]="loggedIn"
    ></challenge-registry-user-profile-stats>
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

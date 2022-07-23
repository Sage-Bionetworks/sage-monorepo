# Figma to Code

## User Profile

In `user-profile.component.html`:

1. Wrap the content between `<main></main>`.
2. Update the `<div class="base-container">` to import the `user` object.
    ```typescript

    ```
3. Add `[routerLink]="." [routerParams]=...` to the `<a>` elements of the menu.

4. Fix Star page

    - Fix the the layout of star page (position, height, weight), which was different from the other two sections (biography/challenges).
    - Discuss with Verena what could be the source of the issue

## Create user-profile-star-stats component

 1. Create a new component `user-profile-stats`. 
 2. Move codes from `basic-stats.html` and `basic-stats.scss` to the `user-profile-stats` component.
 3. Add `loggedIn` variable to determine whether to use the `public` or `loggin` box.
 4. Remove the `top` property in the `.basic-stats-logged-in` and `.basic-stats-public` to correct the position of the box in the page.

 ## Themes:

 1. Create and update `_user-profile-themes.scss` at the `user-profile/src/lib/`. Move all the colors and fonts from `user-profile.component.scss` to `_user-profile-themes.scss`. 
 2. Add and setup `_lib_themes.scss` at the `user-profile/src`.
 3. Load themes of user-profile in `libs/challenge-registry/themes/src/_index.scss`.
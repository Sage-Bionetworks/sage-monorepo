# Figma to Code

## User Profile

In `user-profile.component.html`:

1. Wrap the content between `<main></main>`.
2. Update the `<div class="base-container">` to import the `user` object.
    ```ts

    ```
n. Update the stats box:
 - Create a new component `user-profile-stats`. 
 - Move codes from `basic-stats.html` and `basic-stats.scss` to the `user-profile-stats` component.
 - Add `loggedIn` variable to determine whether to use the `public` or `loggin` box.
 - Remove the `top` property of of the `.basic-stats-logged-in` and `.basic-stats-public` to correct the position of the box in the page.
 
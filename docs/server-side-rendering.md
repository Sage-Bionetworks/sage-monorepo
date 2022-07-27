# Server-side rendering (SSR) with Angular for Nx workspaces

## Challenge Registry app

1. Create a new Angular app.
    ```console
    nx generate @schematics/angular:app challenge-registry --routing=true --style=scss --dry-run
    nx generate @schematics/angular:app challenge-registry --routing=true --style=scss
    ```
1. Create the CR app with the generator `@schematics/angular:universal` developed by the Angular
   team.
    ```console
    nx generate @schematics/angular:universal --project=challenge-registry
    ```
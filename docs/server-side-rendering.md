# Server-side rendering (SSR)

## Create an Angular Universal application

1. Create an Angular app.
    ```console
    nx generate @nrwl/angular:app challenge-registry --routing=true --style=scss
    ```
    > **Note**
    > Add the option `--dry-run` to preview the output of Nx commands.
2. Let's use the Angular Universal schematic to initialize our SSR setup for the
   `challenge-registry` application by running the following in the terminal:
    ```console
    nx generate @schematics/angular:universal --project=challenge-registry
    ```
3. Follow the instructions listed in the article [Server-side rendering (SSR) with Angular for Nx
   workspaces] to add the project target `serve-ssr`.



<!-- Links -->

[Server-side rendering (SSR) with Angular for Nx workspaces]: https://blog.nrwl.io/server-side-rendering-ssr-with-angular-for-nx-workspaces-14e2414ca532
# Server-side rendering (SSR)

## Create an Angular Universal application

1. Create an Angular app.
    ```console
    nx generate @nrwl/angular:app challenge-registry --routing=true --style=scss
    ```
    > **Note** Add the option `--dry-run` to preview the output of Nx commands.

    > **Note** The legacy challenge registry app generated early 2022 was build with the generator
    > `@nrwl/angular:webpack-browser`. The newly generated app is now built with
    > `@angular-devkit/build-angular:browser` (see `project.json`).

2. Let's use the Angular Universal schematic to initialize our SSR setup for the
   `challenge-registry` application by running the following in the terminal:
    ```console
    nx generate @schematics/angular:universal --project=challenge-registry
    ```

3. Follow the instructions listed in the article [Server-side rendering (SSR) with Angular for Nx
   workspaces] to add the project target `serve-ssr`.

4. Delete the folder `src/assets` and `src/environments`. Import the folder `src/assets` and
   `src/config` from the legacy challenge registry app.

5. Update `project.json`
    - Add this line to the top of `project.json` is missing.
      ```console
      "$schema": "../../node_modules/nx/schemas/project-schema.json",
      ```

    - Update the value of `prefix`.
      ```console
      "prefix": "challenge-registry",
      ```

    - Copy-paste the following targets from the legacy app.
      - `prepare`
      - `lint-fix`
      - `build-image`

    - Update the value of `assets` specified in the `build` target.
      ```console
      "assets": [
        "apps/challenge-registry/src/assets",
        "apps/challenge-registry/src/config",
        {
          "input": "libs/shared/assets/src/assets",
          "glob": "**/*",
          "output": "assets"
        },
        {
          "input": "libs/challenge-registry/assets/src/assets",
          "glob": "**/*",
          "output": "challenge-registry-assets"
        },
        {
          "input": "libs/shared/assets/src",
          "glob": "favicon.ico",
          "output": ""
        }
      ],
      ```

    - Update the value of `budgets` in the `configurations` object of the `build` target.
      ```console
      "budgets": [
        {
          "type": "initial",
          "maximumWarning": "500kb",
          "maximumError": "1mb"
        },
        {
          "type": "anyComponentStyle",
          "maximumWarning": "2kb",
          "maximumError": "8kb"
        }
      ],
      ```

    - Empty the array value of `fileReplacements` in the `configurations` object of the
      `build` target.

    - Set the project `tags`.
      ```console
      "tags": [
        "type:app",
        "scope:client"
      ],
      ```

    - Set the project `implicitDependencies`.
      ```console
      "implicitDependencies": [
        "challenge-registry-styles",
        "challenge-registry-themes",
        "shared-assets",
        "challenge-api-gateway",
        "challenge-keycloak"
      ]
      ```

  - Replace the content of the `src/main.ts` with the following code to make use of the
    configuration defined in the folder `src/config` to enable [Build once, deploy many].
    ```console
    import { enableProdMode } from '@angular/core';
    import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';

    import { AppModule } from './app/app.module';
    import {
      AppConfig,
      APP_CONFIG,
      Environment,
    } from '@sagebionetworks/challenge-registry/config';

    function bootstrap() {
      fetch('/config/config.json')
        .then((response) => response.json() as Promise<AppConfig>)
        .then((config: AppConfig) => {
          if (
            [Environment.Production, Environment.Staging].includes(
              config.environment
            )
          ) {
            enableProdMode();
          }

          console.log('challenge-registry config', config);

          platformBrowserDynamic([{ provide: APP_CONFIG, useValue: config }])
            .bootstrapModule(AppModule)
            .catch((err) => console.error(err));
        });
    }

    if (document.readyState === 'complete') {
      bootstrap();
    } else {
      document.addEventListener('DOMContentLoaded', bootstrap);
    }
    ```

  - Update the `src` files based on the legacy code. These files include but are not limited to:
    - `src/app/*`
    - `_app-theme.scss`
    - `src/index.html`
    - `src/proxy.conf.json`
    - `styles.scss`

  - Update the files in the project folder based on their legacy version. These files include but
    are not limited to:
    - `docker/`
    - `.eslintrc.json`
    - `Dockerfile`

<!-- Links -->

[Server-side rendering (SSR) with Angular for Nx workspaces]: https://blog.nrwl.io/server-side-rendering-ssr-with-angular-for-nx-workspaces-14e2414ca532
[Build once, deploy many]: https://12factor.net/codebase

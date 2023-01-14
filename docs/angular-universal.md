# Angular Universal

## Introduction

The article [Angular Universal: a Complete Practical Guide] offers an excellent introduction to
Angular Universal, server-side rendering (SSR) and search engine optimization (SEO).

## Why Angular Universal?

- Improve the startup performance of our application.
- Make our application more search engine friendly.
  - Most search engine crawlers expect these important SEO meta tags to be present on the HTML
    returned by the server, and not to modified at runtime by Javascript as done with client-side
    rendering (CSR).
  - If we are targetting only the Google search engine, as we have shown there is no need to
    server-side render our content in order to have it ranked correctly, as Google can today index
    correctly most Javascript-based content.
- Improve the social media presence of our application.
  - Enable social media crawlers to crawl our application pages.

## About Angular AOT compilation and SRR rendering

> The Angular AOT compiler converts your Angular HTML and TS code into efficient JavaScript code"
> (angular.io/guide/aot-compiler). That code is still run on client-side to render the first and
> every other view. SSR is not an alternative but an additional technique to further increase
> performance for first-time visitors and SEO, as the first view is rendered on server side and the
> client receives that assembled and styled HTML, so there is no need to dynamically render anything
> at the beginning.

Martin Schneider Aug 26, 2021 at 14:15 ([source](https://stackoverflow.com/q/68939599))

## Create an Angular Universal application

We are going to go start with an existing Angular application, and we will progressively turn it
into an Angular Universal application while explaining every step along the way!

The second part of the instructions describe how to tweak the app to meet the need of this project.
The previous version of the OpenChallenges app generated in early 2022 is referenced as the "legacy"
app.

1. Create an Angular app.
    ```console
    nx generate @nrwl/angular:app openchallenges --routing=true --style=scss
    ```
    > **Note** Add the option `--dry-run` to preview the output of Nx commands.

    > **Note** The legacy OpenChallenges app generated early 2022 was build with the generator
    > `@nrwl/angular:webpack-browser`. The newly generated app is now built with
    > `@angular-devkit/build-angular:browser` (see `project.json`).

2. Let's use the Angular Universal schematic to initialize our SSR setup for the
   `openchallenges` application by running the following in the terminal:
    ```console
    nx generate @schematics/angular:universal --project=openchallenges
    ```

3. Follow the instructions listed in the article [Server-side rendering (SSR) with Angular for Nx
   workspaces] to add the project target `serve-ssr`.

4. Delete the folder `src/assets` and `src/environments`. Import the folder `src/assets` and
   `src/config` from the legacy OpenChallenges app.

5. Update `project.json`
    - Add this line to the top of `project.json` is missing.
      ```console
      "$schema": "../../node_modules/nx/schemas/project-schema.json",
      ```

    - Update the value of `prefix`.
      ```console
      "prefix": "openchallenges",
      ```

    - Copy-paste the following targets from the legacy app.
      - `prepare`
      - `lint-fix`
      - `build-image`

    - Update the value of `assets` specified in the `build` target.
      ```console
      "assets": [
        "apps/openchallenges/src/assets",
        "apps/openchallenges/src/config",
        {
          "input": "libs/shared/typescript/assets/src/assets",
          "glob": "**/*",
          "output": "assets"
        },
        {
          "input": "libs/openchallenges/assets/src/assets",
          "glob": "**/*",
          "output": "openchallenges-assets"
        },
        {
          "input": "libs/shared/typescript/assets/src",
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
        "openchallenges-styles",
        "openchallenges-themes",
        "shared-typescript-assets",
        "openchallenges-api-gateway",
        "openchallenges-keycloak"
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
    } from '@sagebionetworks/openchallenges/config';

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

          console.log('openchallenges config', config);

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

## Build the Angular Universal Bundle

```console
nx server openchallenges --prod

$ ls -alh dist/apps/openchallenges/server/
total 5.3M
drwxr-xr-x 2 vscode vscode 4.0K Jul 28 15:39 .
drwxr-xr-x 3 vscode vscode 4.0K Jul 28 15:39 ..
-rw-r--r-- 1 vscode vscode 174K Jul 28 15:39 297.js
-rw-r--r-- 1 vscode vscode 112K Jul 28 15:39 3rdpartylicenses.txt
-rw-r--r-- 1 vscode vscode 6.9K Jul 28 15:39 513.js
-rw-r--r-- 1 vscode vscode  95K Jul 28 15:39 603.js
-rw-r--r-- 1 vscode vscode 6.7K Jul 28 15:39 654.js
-rw-r--r-- 1 vscode vscode 299K Jul 28 15:39 715.js
-rw-r--r-- 1 vscode vscode 174K Jul 28 15:39 731.js
-rw-r--r-- 1 vscode vscode 248K Jul 28 15:39 748.js
-rw-r--r-- 1 vscode vscode  13K Jul 28 15:39 762.js
-rw-r--r-- 1 vscode vscode 7.1K Jul 28 15:39 822.js
-rw-r--r-- 1 vscode vscode 102K Jul 28 15:39 989.js
-rw-r--r-- 1 vscode vscode 4.0M Jul 28 15:39 main.js
```

<!-- > **Note** Production bundles usually include hashes in client-side applications to help the browser
> when it can reuse cached files (e.g. `main.4a7032a52eaa8a28.js`). In SSR application, we prefer to
> have the names of the bundle files constant so that we can import them in the script that starts
> the Express server. -->

From [Angular Universal: a Complete Practical Guide]:

> We will need the production version of the Universal bundle, as the development bundle will not
> work.

Really?

## TODO

To review and adapt:

```console
# Development
npm run start
http://localhost:4200/

# Tests
npm run lint
npm run test
npm run e2e

# AOT Compilation
npm run build

# SSR Compilation
npm run build:ssr
npm run serve:ssr
http://localhost:4000/
```

## References

- [Server-side rendering (SSR) with Angular Universal]
- [Angular Universal: a Complete Practical Guide]
- [Rendering on the Web]
- [Server-side rendering (SSR) with Angular for Nx workspaces]

<!-- Links -->

[Server-side rendering (SSR) with Angular for Nx workspaces]: https://blog.nrwl.io/server-side-rendering-ssr-with-angular-for-nx-workspaces-14e2414ca532
[Build once, deploy many]: https://12factor.net/codebase
[Server-side rendering (SSR) with Angular Universal]: https://angular.io/guide/universal
[Angular Universal: a Complete Practical Guide]: https://blog.angular-university.io/angular-universal/
[Rendering on the Web]: https://web.dev/rendering-on-the-web/

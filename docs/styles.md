Style variables are defined in `libs/shared/style/src/_variables.scss`.

### Option 1

One way to make use of the variables is to add the following to the project file
of `web-app`.

```json
        "stylePreprocessorOptions": {
          "includePaths": [
            "libs/shared/style/src"
          ]
        }
```

This will allow a component style file to include `@use 'variables';`. This is
also valid for components included in imported libraries like `libs/web/ui`.

Ideally, I would prefer a solution where the import would look like `@use
'@challenge-registry/shared-style/src/variables'`.

### Option 2

This soluion enables to import an SCSS file using an informative path. For
example, the style file of a components can include common variables using `@use
'libs/shared/style/src/variables';`. Here, an app consuming the component does
not need to specify `stylePreprocessorOptions`.

### Option 3

Add the following to the `dependencies` section of the workspace `package.json`.

```json
"@challenge-registry/shared-style": "link:libs/shared/style/src"
```

Run `yarn install`.

The style file of a component can now import the variable with `@use
'@challenge-registry/shared-style/src/variables';`.

### Conclusion

I prefer `Option 2` because the import path is straightforward and because this
solution does not require to setup symlink with yarn.










          "node_modules/@angular/material/prebuilt-themes/indigo-pink.css",
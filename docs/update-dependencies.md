# Update Dependencies

## Node.js

Identify whether a new version is available for a package.

```console
yarn outdated <package>
```

Update the package.

```console
yarn upgrade <package> --latest
```

Example:

```console
$ yarn outdated @nxrocks/nx-spring-boot
yarn outdated v1.22.19
info Color legend :
 "<red>"    : Major Update backward-incompatible updates
 "<yellow>" : Minor Update backward-compatible features
 "<green>"  : Patch Update backward-compatible bug fixes
Package                 Current Wanted Latest Package Type    URL
@nxrocks/nx-spring-boot 4.0.2   4.1.0  4.1.0  devDependencies https://github.com/tinesoft/nxrocks/blob/master/packages/nx-spring-boot#readme
Done in 4.26s.

$ yarn upgrade @nxrocks/nx-spring-boot --latest
...
success Saved 2 new dependencies.
info Direct dependencies
└─ @nxrocks/nx-spring-boot@4.1.0
info All dependencies
├─ @nxrocks/common@1.1.0
└─ @nxrocks/nx-spring-boot@4.1.0
Done in 162.06s.
```
# Nx Libraries

## Angular library

### Creating the library

    nx g @nrwl/angular:lib awesome-lib [--dry-run]

The library is added to `tsconfig.base.json`, which will make it available to
other projects.

### Example

Creation of an Angular footer library in `libs/web/ui-footer`.

```console
nx g @nrwl/angular:lib ui --directory web
nx g @nrwl/angular:component footer --project=web-ui
```

## Web Components library

### Creating the library

    nx g @nrwl/web:lib ui-footer [--dry-run]

The library is added to `tsconfig.base.json`, which will make it available to
other projects.

### Fix eslint `no-undef`

ESLint is expected to show errors like the ones shown below. The reason is
because it does not see their definition, which are provided by the DOM. The
solution is to disable the ESLint rule `no-undef` in the `.eslintrc.json` of the
library. TypeScript already provides this check.

```typescript
'HTMLElement' is not defined (eslint `no-undef`)
'customElements' is not defined (eslint `no-undef`)
```

### Importing the library in an Angular app

We need need to register `CUSTOM_ELEMENTS_SCHEMA` in our app or component
module. this will tell the Angular compiler to refrain from throwing errors when
seeing non-standard element tags in our templates.

```typescript
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { NxWelcomeComponent } from './nx-welcome.component';
import { RouterModule } from '@angular/router';

@NgModule({
  declarations: [AppComponent, NxWelcomeComponent],
  imports: [
    BrowserModule,
    RouterModule.forRoot(
      { initialNavigation: 'enabledBlocking' }
    ),
  ],
  providers: [],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  bootstrap: [AppComponent],
})
export class AppModule {}
```

Import the UI library in an Angular component.

```typescript
import '@challenge-registry/ui-footer';
```

The component `ui-welcome` provided by the library can now be used in the HTML
file of our Angular component.

```html
<ui-welcome title="Awesome App"></ui-welcome>
```

### Importing the library in the React app

We need to tell React to allow components that are not defined within it. To
allow this, we will need to create a type file on the root of the `src` folder or
the React project. Name it `intrinsic.d.ts` and add inside the following:

```typescript
declare namespace JSX {
  interface IntrinsicElements {
    [elemName: string]: any;
  }
}
```

Use the UI component `ui-welcome` in React.

```typescript
export function App() {
  return (
    <>
      <ui-welcome title="web-app-react" />
    </>
  );
}

export default App;
```

### References

- [Share Components across Front Ends frameworks using Nx and Web
  Components](https://crocsx.hashnode.dev/share-components-across-front-ends-frameworks-using-nx-and-web-components)
- [Working with Nx.dev using React, Angular and native web components (Part 1 of
  3)](https://medium.com/@fabianandrescano/working-with-nx-dev-9761da40566a)
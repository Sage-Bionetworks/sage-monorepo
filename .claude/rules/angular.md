---
paths:
  - '**/*.ts'
  - '**/*.html'
---

# Angular Development Standards

## Coding Standards

- Always use standalone components over NgModules
- Use signals for reactive state management
- Use `@if`, `@for` control flow instead of `*ngIf`, `*ngFor`
- Write tests with `@testing-library/angular` and `@testing-library/jest-dom`
- Focus tests on user behavior rather than implementation details

## Angular Documentation Reference

Use these links to look up Angular concepts and APIs:

### Components

- [What is a component](https://angular.dev/guide/components)
- [Component selectors](https://angular.dev/guide/components/selectors)
- [Styling components](https://angular.dev/guide/components/styling)
- [Inputs](https://angular.dev/guide/components/inputs) / [Outputs](https://angular.dev/guide/components/outputs)
- [Content projection](https://angular.dev/guide/components/content-projection)
- [Component lifecycle](https://angular.dev/guide/components/lifecycle)

### Templates

- [Template Overview](https://angular.dev/guide/templates)
- [Control Flow](https://angular.dev/guide/templates/control-flow)
- [Binding](https://angular.dev/guide/templates/binding)
- [Event listeners](https://angular.dev/guide/templates/event-listeners)
- [Deferred loading](https://angular.dev/guide/templates/defer)

### Signals

- [Signals overview](https://angular.dev/guide/signals)
- [linkedSignal](https://angular.dev/guide/signals/linked-signal)
- [Async reactivity with resources](https://angular.dev/guide/signals/resource)

### Dependency Injection

- [DI overview](https://angular.dev/guide/di)
- [Creating an injectable service](https://angular.dev/guide/di/creating-injectable-service)
- [Hierarchical injectors](https://angular.dev/guide/di/hierarchical-dependency-injection)

### RxJS

- [RxJS interop with Angular signals](https://angular.dev/ecosystem/rxjs-interop)

### HTTP

- [HttpClient overview](https://angular.dev/guide/http)
- [Making requests](https://angular.dev/guide/http/making-requests)
- [Interceptors](https://angular.dev/guide/http/interceptors)

### Forms

- [Reactive Forms](https://angular.dev/guide/forms/reactive-forms)
- [Strictly typed forms](https://angular.dev/guide/forms/typed-forms)
- [Form validation](https://angular.dev/guide/forms/form-validation)

### Routing

- [Define routes](https://angular.dev/guide/routing/define-routes)
- [Navigate to routes](https://angular.dev/guide/routing/navigate-to-routes)
- [Read route state](https://angular.dev/guide/routing/read-route-state)

### Server Side Rendering

- [SSR with Angular](https://angular.dev/guide/ssr)
- [Hybrid rendering](https://angular.dev/guide/hybrid-rendering)
- [Hydration](https://angular.dev/guide/hydration)

### Testing

- [Testing overview](https://angular.dev/guide/testing)
- [Testing services](https://angular.dev/guide/testing/services)
- [Component testing](https://angular.dev/guide/testing/components-basics)

### Other

- [Zoneless](https://angular.dev/guide/zoneless)
- [Style Guide](https://next.angular.dev/style-guide)
- [API reference](https://angular.dev/api)
- [Error encyclopedia](https://angular.dev/errors)

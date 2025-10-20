# Shared Google Tag Manager Component

A reusable Google Tag Manager integration component for Angular applications in the sage-monorepo.

## Overview

This library provides a standalone Angular component that integrates Google Tag Manager (GTM) into your application. It handles the initialization of GTM and tracks page navigation events automatically.

## Usage

### 1. Update your App Component

Import and configure the `GoogleTagManagerComponent` in your app component:

```typescript
import { Component, inject } from '@angular/core';
import {
  GoogleTagManagerComponent,
  createGoogleTagManagerIdProvider,
  CONFIG_SERVICE_TOKEN,
  isGoogleTagManagerIdSet,
} from '@sagebionetworks/shared/typescript/google-tag-manager';
import { ConfigService } from '@your-project/config';

@Component({
  imports: [
    // other imports
    GoogleTagManagerComponent,
  ],
  providers: [
    {
      provide: CONFIG_SERVICE_TOKEN,
      useFactory: () => inject(ConfigService), // Your app's specific ConfigService
    },
    createGoogleTagManagerIdProvider(),
  ],
  // other component metadata
})
export class AppComponent {
  configService = inject(ConfigService);
  readonly useGoogleTagManager: boolean;

  constructor() {
    this.useGoogleTagManager = isGoogleTagManagerIdSet(
      this.configService.config.googleTagManagerId.length,
    );
  }
}
```

### 2. Add the component to your template

Add the `GoogleTagManagerComponent` to your app component's template:

```
@if (useGoogleTagManager) {
  <sage-google-tag-manager />
}
<!-- rest of your app -->
```

### 3. Configure the Google Tag Manager ID

Make sure your application's config model includes a `googleTagManagerId` property:

```typescript
export interface AppConfig {
  // other config properties
  googleTagManagerId: string;
  // other config properties
}
```

The GTM component will only be active if the `googleTagManagerId` property is set to a non-empty string.

## Features

- Automatically initializes Google Tag Manager
- Tracks page navigation events
- Handles server-side rendering gracefully
- Configurable through your application's config service
- Standalone Angular component for easy integration
- Type-safe dependency injection using Angular's InjectionToken

## Used In

This component is currently used in the following applications:

- Agora
- OpenChallenges
- Model-AD

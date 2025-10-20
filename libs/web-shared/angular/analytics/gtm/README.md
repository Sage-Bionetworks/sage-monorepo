# Google Tag Manager Integration

Angular library for integrating Google Tag Manager (GTM) into web applications.

## Architecture

This library follows the **adapter boundary pattern** to keep it framework-only and config-agnostic:

- **No dependency on `@sagebionetworks/platform/config/angular`**
- Apps provide configuration via the `GTM_CONFIG` injection token
- The library handles GTM initialization and page tracking

## Usage

### 1. Install the Component

Add the GTM component to your app component:

```typescript
// app.component.ts
import { Component } from '@angular/core';
import { GoogleTagManagerComponent } from '@sagebionetworks/web-shared/angular/analytics/gtm';

@Component({
  selector: 'app-root',
  imports: [GoogleTagManagerComponent],
  template: `
    <sage-google-tag-manager />
    <router-outlet />
  `,
})
export class AppComponent {}
```

### 2. Provide GTM Configuration

#### Option A: Using `provideGtm` Helper (Recommended)

```typescript
// app.config.ts
import { ApplicationConfig } from '@angular/core';
import { provideGtm } from '@sagebionetworks/web-shared/angular/analytics/gtm';
import { ConfigService } from './config/config.service';

export const appConfig: ApplicationConfig = {
  providers: [
    // ... other providers
    provideGtm(() => {
      const config = inject(ConfigService);
      return {
        enabled: config.config.google.tagManager.enabled,
        gtmId: config.config.googleTagManagerId,
        isPlatformServer: config.config.isPlatformServer,
      };
    }),
  ],
};
```

#### Option B: Manual Provider Setup

```typescript
// app.component.ts
import { Component, inject } from '@angular/core';
import {
  GoogleTagManagerComponent,
  GTM_CONFIG,
} from '@sagebionetworks/web-shared/angular/analytics/gtm';
import { ConfigService } from './config/config.service';

@Component({
  selector: 'app-root',
  imports: [GoogleTagManagerComponent],
  providers: [
    {
      provide: GTM_CONFIG,
      useFactory: () => {
        const config = inject(ConfigService);
        return {
          enabled: config.config.google.tagManager.enabled,
          gtmId: config.config.googleTagManagerId,
          isPlatformServer: config.config.isPlatformServer,
        };
      },
    },
  ],
  template: `
    <sage-google-tag-manager />
    <router-outlet />
  `,
})
export class AppComponent {}
```

## Configuration

### GtmConfig Interface

```typescript
interface GtmConfig {
  enabled: boolean; // Whether GTM is enabled (if false, no tracking occurs)
  gtmId: string; // GTM Container ID (e.g., 'GTM-XXXXXX')
  isPlatformServer?: boolean; // Whether running on server (SSR)
}
```

### Conditional GTM Loading

To only load GTM when enabled and a valid ID is configured:

```typescript
import { isGtmIdSet } from '@sagebionetworks/web-shared/angular/analytics/gtm';

const gtmEnabled = config.config.google.tagManager.enabled;
const gtmId = config.config.googleTagManagerId;
const useGoogleTagManager = gtmEnabled && isGtmIdSet(gtmId);

// In template:
@if (useGoogleTagManager) {
  <sage-google-tag-manager />
}
```

### Disabling GTM in Development

In your development config (e.g., `application-dev.yaml`):

```yaml
google:
  tagManager:
    enabled: false # Disable GTM in local development
    id: 'GTM-XXXXXX'
```

This prevents HTTPS certificate errors when loading GTM scripts locally.

## Features

- ✅ **Automatic page tracking** - Tracks route navigation events
- ✅ **SSR-safe** - Only initializes GTM on the client side
- ✅ **Config-agnostic** - No dependency on platform config library
- ✅ **Type-safe** - Full TypeScript support with interfaces
- ✅ **Easy integration** - Simple provider functions
- ✅ **Graceful degradation** - Handles ad blockers and script load failures

## How It Works

1. **Component Initialization**: The `GoogleTagManagerComponent` injects the `GTM_CONFIG` token and `GoogleTagManagerService`
2. **Configuration Check**: Validates that GTM is enabled and has a valid ID
3. **Server Detection**: Checks `isPlatformServer` to avoid running on the server
4. **Route Tracking**: Subscribes to router `NavigationEnd` events
5. **Event Tracking**: Sends page events to GTM data layer via `pushTag()`
6. **Error Handling**: Gracefully handles script load failures (ad blockers, certificate errors)

## Dependencies

- `angular-google-tag-manager` - Third-party GTM Angular library
- `@angular/router` - For navigation event tracking

## Scope & Tags

- **Scope**: `global` - Reusable across all products
- **Layer**: `web-shared` - Frontend-only, cross-product
- **Tech**: `angular` - Angular-specific implementation
- **Type**: `ui` - UI component library

## Example: OpenChallenges Integration

```typescript
// apps/openchallenges/app/src/app/app.config.ts
import { ApplicationConfig, inject } from '@angular/core';
import { provideGtm, isGtmIdSet } from '@sagebionetworks/web-shared/angular/analytics/gtm';
import { ConfigService } from '@sagebionetworks/openchallenges/web/angular/config';

export const appConfig: ApplicationConfig = {
  providers: [
    provideGtm(() => {
      const config = inject(ConfigService);
      return {
        gtmId: config.config.googleTagManagerId,
        isPlatformServer: config.config.isPlatformServer,
      };
    }),
  ],
};
```

```typescript
// apps/openchallenges/app/src/app/app.component.ts
import { Component, inject } from '@angular/core';
import {
  GoogleTagManagerComponent,
  isGtmIdSet,
} from '@sagebionetworks/web-shared/angular/analytics/gtm';
import { ConfigService } from '@sagebionetworks/openchallenges/web/angular/config';

@Component({
  selector: 'app-root',
  imports: [GoogleTagManagerComponent],
  template: `
    @if (useGoogleTagManager) {
      <sage-google-tag-manager />
    }
    <router-outlet />
  `,
})
export class AppComponent {
  private configService = inject(ConfigService);
  readonly useGoogleTagManager = isGtmIdSet(this.configService.config.googleTagManagerId);
}
```

## Related Libraries

- `@sagebionetworks/platform/config/angular` - Platform configuration loader (apps use this to get config, then pass to GTM)

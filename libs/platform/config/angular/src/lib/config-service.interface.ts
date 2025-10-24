import { InjectionToken } from '@angular/core';

/**
 * Generic interface for application configuration services.
 * This interface defines the contract that any config service must implement,
 * allowing libraries to depend on configuration without knowing the specific
 * implementation details.
 *
 * @template T The type of configuration object this service provides
 *
 * @example
 * ```typescript
 * // In a library that needs config access:
 * export interface MyLibraryConfig {
 *   apiUrl: string;
 *   timeout: number;
 * }
 *
 * // Library expects any service implementing this interface:
 * constructor(@Inject(APP_CONFIG_SERVICE) private config: AppConfigService<MyLibraryConfig>) {
 *   const url = this.config.config.apiUrl;
 * }
 *
 * // In the application:
 * providers: [
 *   {
 *     provide: APP_CONFIG_SERVICE,
 *     useExisting: MyAppConfigService, // App's actual config service
 *   },
 * ]
 * ```
 */
export interface AppConfigService<T = unknown> {
  readonly config: T;
}

/**
 * Injection token for the application's config service.
 *
 * This token allows libraries to request configuration from the application
 * without creating a direct dependency on the application's specific config service.
 * It implements the Dependency Inversion Principle: libraries depend on the
 * abstraction (AppConfigService interface) rather than concrete implementations.
 *
 * Applications must provide their config service using this token to make it
 * available to libraries that need configuration access.
 *
 * @example
 * ```typescript
 * // In app.component.ts or app.config.ts:
 * import { APP_CONFIG_SERVICE } from '@sagebionetworks/platform/config/angular';
 * import { ConfigService } from './config/config.service';
 *
 * providers: [
 *   {
 *     provide: APP_CONFIG_SERVICE,
 *     useExisting: ConfigService, // Your app's config service
 *   },
 * ]
 * ```
 */
export const APP_CONFIG_SERVICE = new InjectionToken<AppConfigService>('AppConfigService');

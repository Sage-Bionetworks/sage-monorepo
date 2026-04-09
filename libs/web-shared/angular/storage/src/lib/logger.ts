import { InjectionToken } from '@angular/core';

export interface Logger {
  error(message: string, error?: unknown): void;
}

export const LOGGER = new InjectionToken<Logger>('Logger', {
  providedIn: 'root',
  factory: () => ({
    error: (message: string, error?: unknown) => console.error(message, error),
  }),
});

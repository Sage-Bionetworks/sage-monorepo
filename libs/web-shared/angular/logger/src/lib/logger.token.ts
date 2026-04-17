import { InjectionToken } from '@angular/core';

export type Logger = {
  log(message: string, data?: Record<string, unknown>): void;
  warn(message: string, data?: Record<string, unknown>): void;
  error(message: string, error?: unknown): void;
};

export const LOGGER = new InjectionToken<Logger>('Logger', {
  providedIn: 'root',
  factory: () => ({
    log: (message: string, data?: Record<string, unknown>) => console.log(message, data),
    warn: (message: string, data?: Record<string, unknown>) => console.warn(message, data),
    error: (message: string, error?: unknown) => console.error(message, error),
  }),
});

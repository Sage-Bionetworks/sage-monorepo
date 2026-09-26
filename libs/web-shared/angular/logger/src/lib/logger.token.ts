import { InjectionToken } from '@angular/core';

/**
 * Context for an error log: the caught error, structured data, or both. At least one is required,
 * so every error log explains itself.
 */
export type ErrorLogContext =
  | { error: unknown; data?: Record<string, unknown> }
  | { error?: never; data: Record<string, unknown> };

/**
 * A logger bound to one source. `warn` and `error` messages name the kind of problem and stay
 * fixed; values that vary per occurrence belong in `data`. Implementations that report to an error
 * tracker group events by source and message, so interpolating a varying value into the message
 * splits one problem across many issues.
 */
export type Logger = {
  log(message: string, data?: Record<string, unknown>): void;
  warn(message: string, data?: Record<string, unknown>): void;
  error(message: string, context: ErrorLogContext): void;
};

/**
 * Creates loggers bound to a source: the name of the class, or of the exported function for
 * functional guards and interceptors, that does the logging. Pass it as a string literal, since
 * production builds minify class names.
 */
export type LoggerFactory = {
  forSource(source: string): Logger;
};

export const LOGGER = new InjectionToken<LoggerFactory>('Logger', {
  providedIn: 'root',
  factory: () => ({
    forSource: (source: string) => ({
      log: (message: string, data?: Record<string, unknown>) =>
        console.log(`${source}: ${message}`, data),
      warn: (message: string, data?: Record<string, unknown>) =>
        console.warn(`${source}: ${message}`, data),
      error: (message: string, { error, data }: ErrorLogContext) =>
        console.error(`${source}: ${message}`, error, data),
    }),
  }),
});

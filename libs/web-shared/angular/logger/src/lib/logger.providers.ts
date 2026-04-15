import { Provider, Type } from '@angular/core';
import { Logger, LOGGER } from './logger.token';

export function provideLogger(implementation?: Type<Logger>): Provider {
  if (implementation) {
    return { provide: LOGGER, useExisting: implementation };
  }
  return {
    provide: LOGGER,
    useValue: {
      log: (msg: string, data?: Record<string, unknown>) => console.log(msg, data),
      warn: (msg: string, data?: Record<string, unknown>) => console.warn(msg, data),
      error: (msg: string, err?: unknown) => console.error(msg, err),
    },
  };
}

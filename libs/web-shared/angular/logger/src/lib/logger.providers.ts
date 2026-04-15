import { Provider, Type } from '@angular/core';
import { Logger, LOGGER } from './logger.token';

export function provideLogger(implementation: Type<Logger>): Provider {
  return { provide: LOGGER, useExisting: implementation };
}

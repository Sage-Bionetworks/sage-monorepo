import { Provider, Type } from '@angular/core';
import { LOGGER, LoggerFactory } from './logger';

export function provideLogger(implementation: Type<LoggerFactory>): Provider {
  return { provide: LOGGER, useExisting: implementation };
}

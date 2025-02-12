import { InjectionToken } from '@angular/core';
import Rollbar from 'rollbar';

const rollbarConfig = {
  accessToken: 'e788198867474855a996485580b08d03',
  captureUncaught: true,
  captureUnhandledRejections: true,
};

export function rollbarFactory() {
  return new Rollbar(rollbarConfig);
}

export const RollbarService = new InjectionToken<Rollbar>('rollbar');

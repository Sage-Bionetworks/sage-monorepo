import { InjectionToken } from '@angular/core';
import * as Rollbar from 'rollbar';

const rollbarConfig = {
  accessToken: process.env['ROLLBAR_TOKEN'],
  captureUncaught: true,
  captureUnhandledRejections: true,
};

export function rollbarFactory() {
  return new Rollbar(rollbarConfig);
}

export const RollbarService = new InjectionToken<Rollbar>('rollbar');

import { Injectable, isDevMode } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class LoggerService {
  log(message: string, ...args: any[]) {
    if (isDevMode()) {
      console.log('[LOG]', message, ...args);
    }
  }

  warn(message: string, ...args: any[]) {
    console.warn('[WARN]', message, ...args);
  }

  error(message: string, ...args: any[]) {
    console.error('[ERROR]', message, ...args);
  }

  // Send to remote server
  trackError(err: any) {
    // TODO implement remote logging https://sagebionetworks.jira.com/browse/MG-230
  }
}

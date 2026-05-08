import { inject, Injectable, isDevMode } from '@angular/core';
import { ConfigService } from '@sagebionetworks/bixarena/config';

type LogLevel = 'debug' | 'info' | 'warn' | 'error';

const LEVEL_ORDER: Record<LogLevel, number> = { debug: 0, info: 1, warn: 2, error: 3 };

@Injectable({ providedIn: 'root' })
export class LoggerService {
  private readonly configService = inject(ConfigService);

  private allows(level: LogLevel): boolean {
    const configured = this.configService.config?.logging?.level ?? 'info';
    return LEVEL_ORDER[level] >= LEVEL_ORDER[configured];
  }

  // isDevMode() is a hard floor: debug never outputs in prod builds even if YAML is misconfigured.
  debug(message: string, data?: unknown): void {
    if (!isDevMode() || !this.allows('debug')) return;
    if (data !== undefined) {
      console.debug(message, data);
    } else {
      console.debug(message);
    }
  }

  info(message: string, data?: unknown): void {
    if (!this.allows('info')) return;
    if (data !== undefined) {
      console.info(message, data);
    } else {
      console.info(message);
    }
  }

  warn(message: string, data?: unknown): void {
    if (!this.allows('warn')) return;
    if (data !== undefined) {
      console.warn(message, data);
    } else {
      console.warn(message);
    }
  }

  error(message: string, error?: unknown): void {
    if (!this.allows('error')) return;
    if (error !== undefined) {
      console.error(message, error);
    } else {
      console.error(message);
    }
  }
}

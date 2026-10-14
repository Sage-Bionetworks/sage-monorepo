import { TestBed } from '@angular/core/testing';
import { ConfigService } from '@sagebionetworks/bixarena/config';
import { LoggerService } from './logger.service';

const SOURCE = 'LeaderboardFacadeService';
const ERROR_MESSAGE = 'Failed to fetch leaderboard data';
const TITLE = `${SOURCE}: ${ERROR_MESSAGE}`;

function setup(level: 'debug' | 'info' | 'warn' | 'error' = 'info') {
  TestBed.configureTestingModule({
    providers: [{ provide: ConfigService, useValue: { config: { logging: { level } } } }],
  });
  return TestBed.inject(LoggerService).forSource(SOURCE);
}

describe('LoggerService', () => {
  let consoleError: jest.SpyInstance;
  let consoleWarn: jest.SpyInstance;

  beforeEach(() => {
    consoleError = jest.spyOn(console, 'error').mockImplementation();
    consoleWarn = jest.spyOn(console, 'warn').mockImplementation();
  });

  afterEach(() => {
    jest.restoreAllMocks();
  });

  describe('error', () => {
    const cause = new Error('boom');
    const data = { leaderboardId: 'overall', query: { pageSize: 50 } };

    it('should write the source-prefixed message, error, and data to the console', () => {
      setup().error(ERROR_MESSAGE, { error: cause, data });

      expect(consoleError).toHaveBeenCalledWith(TITLE, cause, data);
    });

    it('should omit the data when only an error is given', () => {
      setup().error(ERROR_MESSAGE, { error: cause });

      expect(consoleError).toHaveBeenCalledWith(TITLE, cause);
    });

    it('should omit the error when only data is given', () => {
      setup().error(ERROR_MESSAGE, { data });

      expect(consoleError).toHaveBeenCalledWith(TITLE, data);
    });

    it('should still write at the strictest configured level', () => {
      setup('error').error(ERROR_MESSAGE, { error: cause });

      expect(consoleError).toHaveBeenCalledTimes(1);
    });
  });

  describe('level gating', () => {
    it('should suppress output below the configured level', () => {
      setup('error').warn('Leaderboard snapshot is stale');

      expect(consoleWarn).not.toHaveBeenCalled();
    });

    it('should write output at or above the configured level', () => {
      setup('warn').warn('Leaderboard snapshot is stale');

      expect(consoleWarn).toHaveBeenCalledWith(`${SOURCE}: Leaderboard snapshot is stale`);
    });
  });
});

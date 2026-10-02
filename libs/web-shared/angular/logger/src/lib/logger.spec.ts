import { TestBed } from '@angular/core/testing';
import { LOGGER } from './logger';

const SOURCE = 'LocalStorageService';
const MESSAGE = 'Failed to read from localStorage';
const TITLE = `${SOURCE}: ${MESSAGE}`;

function setup() {
  return TestBed.inject(LOGGER).forSource(SOURCE);
}

describe('LOGGER default factory', () => {
  const cause = new Error('QuotaExceededError');
  const data = { key: 'pinnedItems' };

  afterEach(() => {
    jest.restoreAllMocks();
  });

  it('should write the source-prefixed message and data on log', () => {
    const consoleLog = jest.spyOn(console, 'log').mockImplementation();

    setup().log(MESSAGE, data);

    expect(consoleLog).toHaveBeenCalledWith(TITLE, data);
  });

  it('should write only the source-prefixed message when log has no data', () => {
    const consoleLog = jest.spyOn(console, 'log').mockImplementation();

    setup().log(MESSAGE);

    expect(consoleLog).toHaveBeenCalledWith(TITLE);
  });

  it('should write only the source-prefixed message when warn has no data', () => {
    const consoleWarn = jest.spyOn(console, 'warn').mockImplementation();

    setup().warn(MESSAGE);

    expect(consoleWarn).toHaveBeenCalledWith(TITLE);
  });

  it('should write the source-prefixed message, error, and data on error', () => {
    const consoleError = jest.spyOn(console, 'error').mockImplementation();

    setup().error(MESSAGE, { error: cause, data });

    expect(consoleError).toHaveBeenCalledWith(TITLE, cause, data);
  });

  it('should omit the error when only data is given', () => {
    const consoleError = jest.spyOn(console, 'error').mockImplementation();

    setup().error(MESSAGE, { data });

    expect(consoleError).toHaveBeenCalledWith(TITLE, data);
  });

  it('should omit the data when only an error is given', () => {
    const consoleError = jest.spyOn(console, 'error').mockImplementation();

    setup().error(MESSAGE, { error: cause });

    expect(consoleError).toHaveBeenCalledWith(TITLE, cause);
  });
});

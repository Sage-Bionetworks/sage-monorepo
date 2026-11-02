import { isChunkLoadError } from './chunk-load-error';

const chunkUrl = 'https://modeladexplorer.org/chunk-ABCD1234.js';

describe('isChunkLoadError', () => {
  it.each([
    ['Chrome', `Failed to fetch dynamically imported module: ${chunkUrl}`],
    ['Firefox', `error loading dynamically imported module: ${chunkUrl}`],
    ['Safari', 'Importing a module script failed.'],
  ])('recognizes the %s chunk-load error', (_browser, message) => {
    expect(isChunkLoadError(new TypeError(message))).toBe(true);
  });

  it('does not match other errors', () => {
    expect(isChunkLoadError(new TypeError('Cannot read properties of undefined'))).toBe(false);
  });

  it('does not match values that are not errors', () => {
    expect(isChunkLoadError('Failed to fetch dynamically imported module')).toBe(false);
    expect(isChunkLoadError(undefined)).toBe(false);
  });
});

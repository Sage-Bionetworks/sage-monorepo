import type { Request, Response } from 'express';
import { CHUNK_FILE_PATH_PATTERN, createMissingChunkHandler } from './missing-chunk';

const missingChunkPath = '/chunk-ABCD1234.js';

describe('CHUNK_FILE_PATH_PATTERN', () => {
  it('matches a chunk file name the production build produces', () => {
    expect(CHUNK_FILE_PATH_PATTERN.test(missingChunkPath)).toBe(true);
  });

  it.each([
    ['the main bundle', '/main-ABCD1234.js'],
    ['a lowercase hash', '/chunk-abcd1234.js'],
    ['a hash of the wrong length', '/chunk-ABC123.js'],
    ['a chunk in a subfolder', '/assets/chunk-ABCD1234.js'],
    ['a chunk source map', '/chunk-ABCD1234.js.map'],
  ])('does not match %s', (_description, path) => {
    expect(CHUNK_FILE_PATH_PATTERN.test(path)).toBe(false);
  });
});

describe('createMissingChunkHandler', () => {
  it('responds with an uncached 404', () => {
    const res = { set: jest.fn(), sendStatus: jest.fn() };
    res.set.mockReturnValue(res);

    createMissingChunkHandler()({} as Request, res as unknown as Response);

    expect(res.set).toHaveBeenCalledWith('Cache-Control', 'no-store');
    expect(res.sendStatus).toHaveBeenCalledWith(404);
  });
});

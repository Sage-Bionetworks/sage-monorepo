import { CapitalizeFirstLetterPipe } from './capitalize-first-letter.pipe';

describe('CapitalizeFirstLetterPipe', () => {
  let pipe: CapitalizeFirstLetterPipe;

  beforeEach(() => {
    pipe = new CapitalizeFirstLetterPipe();
  });

  it('should return empty string when input is null or undefined', () => {
    expect(pipe.transform(null)).toBe('');
    expect(pipe.transform(undefined)).toBe('');
  });

  it('should return empty string when input is empty', () => {
    expect(pipe.transform('')).toBe('');
  });

  it('should capitalize the first letter of a lowercase string', () => {
    expect(pipe.transform('hello world')).toBe('Hello world');
  });

  it('should leave an already-capitalized string unchanged', () => {
    expect(pipe.transform('Hello world')).toBe('Hello world');
  });

  it('should not alter casing of subsequent characters', () => {
    expect(pipe.transform('hello WORLD')).toBe('Hello WORLD');
  });

  it('should handle a single character', () => {
    expect(pipe.transform('a')).toBe('A');
  });
});

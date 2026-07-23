import { capitalizeFirstLetter } from './capitalize-first-letter';

describe('capitalizeFirstLetter', () => {
  it('should return empty string when input is null or undefined', () => {
    expect(capitalizeFirstLetter(null)).toBe('');
    expect(capitalizeFirstLetter(undefined)).toBe('');
  });

  it('should return empty string when input is empty', () => {
    expect(capitalizeFirstLetter('')).toBe('');
  });

  it('should capitalize the first letter of a lowercase string', () => {
    expect(capitalizeFirstLetter('hello world')).toBe('Hello world');
  });

  it('should leave an already-capitalized string unchanged', () => {
    expect(capitalizeFirstLetter('Hello world')).toBe('Hello world');
  });

  it('should not alter casing of subsequent characters', () => {
    expect(capitalizeFirstLetter('hello WORLD')).toBe('Hello WORLD');
  });

  it('should handle a single character', () => {
    expect(capitalizeFirstLetter('a')).toBe('A');
  });
});

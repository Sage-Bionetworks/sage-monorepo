import { getRandomInt, isExternalLink, removeParentheses, toKebabCase } from './helpers';

describe('getRandomInt', () => {
  it('should return an integer within the specified range', () => {
    const min = 1;
    const max = 5;
    const result = getRandomInt(min, max);
    expect(result).toBeGreaterThanOrEqual(min);
    expect(result).toBeLessThan(max);
  });

  it('should handle cases where minInclusive equals maxExclusive - 1', () => {
    const min = 5;
    const max = 6;
    const result = getRandomInt(min, max);
    expect(result).toEqual(5);
  });
});

describe('removeParentheses', () => {
  it('should remove parentheses from a string', () => {
    const input = 'test(value)';
    const expected = 'testvalue';
    const result = removeParentheses(input);
    expect(result).toEqual(expected);
  });

  it('should handle strings without parentheses', () => {
    const input = 'testvalue';
    const expected = 'testvalue';
    const result = removeParentheses(input);
    expect(result).toEqual(expected);
  });

  it('should handle empty strings', () => {
    const input = '';
    const expected = '';
    const result = removeParentheses(input);
    expect(result).toEqual(expected);
  });
});

describe('toKebabCase', () => {
  it('should convert a string to kebab-case', () => {
    const input = 'Hello World';
    const expected = 'hello-world';
    const result = toKebabCase(input);
    expect(result).toEqual(expected);
  });

  it('should handle strings with multiple spaces', () => {
    const input = 'Hello   World   Test';
    const expected = 'hello-world-test';
    const result = toKebabCase(input);
    expect(result).toEqual(expected);
  });

  it('should handle empty strings', () => {
    const input = '';
    const expected = '';
    const result = toKebabCase(input);
    expect(result).toEqual(expected);
  });
});

describe('isExternalLink', () => {
  it('should return true for external links', () => {
    expect(isExternalLink('http://example.com')).toBe(true);
    expect(isExternalLink('https://example.com')).toBe(true);
    expect(isExternalLink('  https://example.com  ')).toBe(true);
  });

  it('should return false for internal links', () => {
    expect(isExternalLink('/internal/path')).toBe(false);
    expect(isExternalLink('internal-path')).toBe(false);
    expect(isExternalLink('#anchor')).toBe(false);
  });

  it('should return false for empty strings as this could be intended for /home', () => {
    expect(isExternalLink('')).toBe(false);
    expect(isExternalLink('   ')).toBe(false);
  });
});

import { getRandomInt, removeParenthesis, toKebabCase } from './functions';

describe('getRandomInt', () => {
  it('should return an integer within the specified range', () => {
    const min = 1;
    const max = 5;
    const result = getRandomInt(min, max);
    expect(result).toBeGreaterThanOrEqual(min);
    expect(result).toBeLessThan(max);
  });

  it('should handle cases where minInclusive equals maxExclusive - 1', () => {
    const min = 3;
    const max = 4;
    const result = getRandomInt(min, max);
    expect(result).toBe(min);
  });
});

describe('removeParenthesis', () => {
  it('should remove parentheses from a string', () => {
    const input = '(example)';
    const result = removeParenthesis(input);
    expect(result).toBe('example');
  });

  it('should handle strings without parentheses', () => {
    const input = 'example';
    const result = removeParenthesis(input);
    expect(result).toBe('example');
  });

  it('should handle empty strings', () => {
    const input = '';
    const result = removeParenthesis(input);
    expect(result).toBe('');
  });
});

describe('toKebabCase', () => {
  it('should convert a string to kebab-case', () => {
    const input = 'Hello World';
    const result = toKebabCase(input);
    expect(result).toBe('hello-world');
  });

  it('should handle strings with multiple spaces', () => {
    const input = 'Hello   World';
    const result = toKebabCase(input);
    expect(result).toBe('hello-world');
  });

  it('should handle empty strings', () => {
    const input = '';
    const result = toKebabCase(input);
    expect(result).toBe('');
  });
});

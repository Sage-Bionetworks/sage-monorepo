import {
  escapeRegexChars,
  getRandomInt,
  isExternalLink,
  removeParentheses,
  toKebabCase,
} from './helpers';

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

describe('escapeRegexChars', () => {
  it('should escape regex special characters to prevent injection', () => {
    const input = '.*+?^${}()|[]\\#test';
    const result = escapeRegexChars(input);
    expect(result).toBe('\\.\\*\\+\\?\\^\\$\\{\\}\\(\\)\\|\\[\\]\\\\\\#test');
  });

  it('should escape dash character at the beginning to prevent character class', () => {
    const inputWithDash = '-test';
    const result = escapeRegexChars(inputWithDash);
    expect(result).toBe('\\-test');
  });

  it('should escape square brackets to prevent character class', () => {
    const inputWithBrackets = '[abc]';
    const result = escapeRegexChars(inputWithBrackets);
    expect(result).toBe('\\[abc\\]');
  });

  it('should escape parentheses to prevent capturing groups', () => {
    const inputWithParens = '(test)';
    const result = escapeRegexChars(inputWithParens);
    expect(result).toBe('\\(test\\)');
  });

  it('should escape asterisk and plus to prevent quantifiers', () => {
    const inputWithQuantifiers = 'a*b+';
    const result = escapeRegexChars(inputWithQuantifiers);
    expect(result).toBe('a\\*b\\+');
  });

  it('should escape question mark to prevent optional quantifier', () => {
    const inputWithOptional = 'test?';
    const result = escapeRegexChars(inputWithOptional);
    expect(result).toBe('test\\?');
  });

  it('should escape pipe character to prevent alternation', () => {
    const inputWithPipe = 'test|other';
    const result = escapeRegexChars(inputWithPipe);
    expect(result).toBe('test\\|other');
  });

  it('should escape backslash to prevent escape sequences', () => {
    const inputWithBackslash = 'test\\d';
    const result = escapeRegexChars(inputWithBackslash);
    expect(result).toBe('test\\\\d');
  });

  it('should escape whitespace characters', () => {
    const inputWithWhitespace = 'test space';
    const result = escapeRegexChars(inputWithWhitespace);
    expect(result).toBe('test\\ space');
  });

  it('should escape dot and comma characters', () => {
    const inputWithDotComma = 'test.com,value';
    const result = escapeRegexChars(inputWithDotComma);
    expect(result).toBe('test\\.com\\,value');
  });

  it('should escape caret and hash characters', () => {
    const inputWithCaretHash = '^start#end';
    const result = escapeRegexChars(inputWithCaretHash);
    expect(result).toBe('\\^start\\#end');
  });

  it('should not modify normal alphanumeric characters', () => {
    const normalInput = 'abc123XYZ';
    const result = escapeRegexChars(normalInput);
    expect(result).toBe('abc123XYZ');
  });

  it('should handle empty string', () => {
    const emptyInput = '';
    const result = escapeRegexChars(emptyInput);
    expect(result).toBe('');
  });

  it('should handle hyphen in middle of string', () => {
    const inputWithHyphen = 'test-value';
    const result = escapeRegexChars(inputWithHyphen);
    expect(result).toBe('test\\-value');
  });

  it('should escape all instances of special characters', () => {
    const inputWithMultiples = '*.+*?^$';
    const result = escapeRegexChars(inputWithMultiples);
    expect(result).toBe('\\*\\.\\+\\*\\?\\^\\$');
  });

  it('should handle mixed alphanumeric and special characters', () => {
    const mixedInput = 'user@domain.com (test)';
    const result = escapeRegexChars(mixedInput);
    expect(result).toBe('user@domain\\.com\\ \\(test\\)');
  });

  it('should escape curly braces used in quantifiers', () => {
    const inputWithBraces = 'a{2,4}';
    const result = escapeRegexChars(inputWithBraces);
    expect(result).toBe('a\\{2\\,4\\}');
  });

  it('should handle common ReDoS attack patterns', () => {
    const redosPattern1 = '(a+)+';
    const result1 = escapeRegexChars(redosPattern1);
    expect(result1).toBe('\\(a\\+\\)\\+');

    const redosPattern2 = '(a*)*';
    const result2 = escapeRegexChars(redosPattern2);
    expect(result2).toBe('\\(a\\*\\)\\*');

    const redosPattern3 = '(a|a)*';
    const result3 = escapeRegexChars(redosPattern3);
    expect(result3).toBe('\\(a\\|a\\)\\*');
  });
});

import { formatAppVersion, removeParenthesis } from './app-helpers';

describe('removeParenthesis', () => {
  it('should remove parenthesis from a string', () => {
    const input = 'test(123)';
    const expected = 'test123';
    expect(removeParenthesis(input)).toEqual(expected);
  });
});

describe('formatAppVersion', () => {
  it('should remove -rcX suffix from appVersion', () => {
    const input = '1.0.0-rc1';
    const expected = '1.0.0';
    expect(formatAppVersion(input)).toEqual(expected);
  });
});

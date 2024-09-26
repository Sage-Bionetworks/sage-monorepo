import { removeParenthesis as RemoveParenthesis } from './app-helpers';

describe('removeParenthesis', () => {
  it('should remove parenthesis from a string', () => {
    const input = 'test(123)';
    const expected = 'test123';
    expect(RemoveParenthesis(input)).toEqual(expected);
  });
});

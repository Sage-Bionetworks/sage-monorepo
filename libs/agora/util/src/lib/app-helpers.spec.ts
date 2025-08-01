import { formatAppVersion } from './app-helpers';

describe('formatAppVersion', () => {
  it('should remove -rcX suffix from appVersion', () => {
    const input = '1.0.0-rc1';
    const expected = '1.0.0';
    expect(formatAppVersion(input)).toEqual(expected);
  });
});

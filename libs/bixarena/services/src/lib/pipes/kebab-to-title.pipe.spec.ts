import { KebabToTitlePipe } from './kebab-to-title.pipe';

describe('KebabToTitlePipe', () => {
  const pipe = new KebabToTitlePipe();

  it('converts a kebab-case string to title case with spaces', () => {
    expect(pipe.transform('open-source')).toBe('Open Source');
    expect(pipe.transform('cancer-biology')).toBe('Cancer Biology');
  });

  it('passes single-word values through with title casing', () => {
    expect(pipe.transform('commercial')).toBe('Commercial');
  });

  it('returns an empty string for null, undefined, or empty input', () => {
    expect(pipe.transform(null)).toBe('');
    expect(pipe.transform(undefined)).toBe('');
    expect(pipe.transform('')).toBe('');
  });
});

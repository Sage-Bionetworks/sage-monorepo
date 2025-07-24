import { DecodeGreekEntityPipe } from './decode-greek-entity.pipe';

describe('DecodeGreekEntityPipe', () => {
  let pipe: DecodeGreekEntityPipe;

  beforeEach(() => {
    pipe = new DecodeGreekEntityPipe();
  });

  it('should return empty string when input is null or undefined', () => {
    expect(pipe.transform(null)).toBe('');
    expect(pipe.transform(undefined)).toBe('');
  });

  it('should return the same string when no Greek entities are present', () => {
    expect(pipe.transform('Hello, World!')).toBe('Hello, World!');
  });

  it('should decode a single Greek entity', () => {
    expect(pipe.transform('&alpha;')).toBe('\u03B1');
    expect(pipe.transform('&beta;')).toBe('\u03B2');
    expect(pipe.transform('&gamma;')).toBe('\u03B3');
  });

  it('should decode multiple Greek entities in a string', () => {
    expect(pipe.transform('&alpha;&beta;&gamma;')).toBe('\u03B1\u03B2\u03B3');
    expect(pipe.transform('&alpha;&beta;&alpha;')).toBe('\u03B1\u03B2\u03B1');
    expect(pipe.transform('&Alpha;&alpha;&Beta;&beta;&Gamma;&gamma;&Delta;&delta;&Tau;&tau;')).toBe(
      '\u0391\u03B1\u0392\u03B2\u0393\u03B3\u0394\u03B4\u03A4\u03C4',
    );
  });

  it('should decode Greek entities mixed with regular text', () => {
    expect(pipe.transform('&Alpha; and &beta;')).toBe('\u0391 and \u03B2');
    expect(pipe.transform('The &delta; value is 0.05')).toBe('The \u03B4 value is 0.05');
  });
});

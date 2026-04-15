import { csvDataToString } from './csv';

describe('csvDataToString', () => {
  it('should return an empty string for empty input', () => {
    expect(csvDataToString([])).toBe('');
  });

  it('should format a header row', () => {
    expect(csvDataToString([['age', 'sex', 'value']])).toBe('"age","sex","value"\n');
  });

  it('should format multiple rows', () => {
    const data = [
      ['age', 'sex', 'value'],
      ['4 months', 'Female', '42.5'],
      ['6 months', 'Male', '55.1'],
    ];
    expect(csvDataToString(data)).toBe(
      '"age","sex","value"\n"4 months","Female","42.5"\n"6 months","Male","55.1"\n',
    );
  });

  it('should escape double quotes within values', () => {
    expect(csvDataToString([['say "hello"', 'normal']])).toBe('"say ""hello""","normal"\n');
  });

  it('should handle values containing commas', () => {
    expect(csvDataToString([['one, two', 'three']])).toBe('"one, two","three"\n');
  });

  it('should handle empty string values', () => {
    expect(csvDataToString([['', 'value', '']])).toBe('"","value",""\n');
  });
});

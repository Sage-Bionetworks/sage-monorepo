import { CSV_MIME_TYPE, csvDataToBlob, csvDataToString } from './csv';

describe('csvDataToString', () => {
  it('should double quote and escape values', () => {
    expect(csvDataToString([['a', 'b', 'c']])).toBe('"a","b","c"\n');
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

  it('should handle empty array', () => {
    expect(csvDataToString([])).toBe('');
  });

  it('should handle a row with no columns', () => {
    expect(csvDataToString([[]])).toBe('\n');
  });

  it('should handle embedded quotes', () => {
    expect(csvDataToString([['a"b', 'c']])).toBe('"a""b","c"\n');
  });

  it('should handle commas and newlines', () => {
    expect(csvDataToString([['a,b', 'c\nd']])).toBe('"a,b","c\nd"\n');
  });

  it('should handle empty string', () => {
    expect(csvDataToString([['']])).toBe('""\n');
  });
});

describe('csvDataToBlob', () => {
  it('should wrap the formatted csv in a blob with the csv mime type', async () => {
    const blob = csvDataToBlob([['a', 'b']]);
    expect(blob.type).toBe(CSV_MIME_TYPE);
    expect(await blob.text()).toBe('"a","b"\n');
  });

  it('should produce an empty blob for empty data', async () => {
    const blob = csvDataToBlob([]);
    expect(blob.size).toBe(0);
  });
});

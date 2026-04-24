/**
 * Converts a 2D array of strings into a CSV-formatted string.
 * Values are quoted and internal double-quotes are escaped.
 */
export function csvDataToString(data: string[][]): string {
  return data
    .map((row) => row.map((v) => `"${v.replaceAll('"', '""')}"`).join(',') + '\n')
    .join('');
}

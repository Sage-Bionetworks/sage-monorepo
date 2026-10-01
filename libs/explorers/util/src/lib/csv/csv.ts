/**
 * Converts a 2D array of strings into a CSV-formatted string.
 * Values are quoted and internal double-quotes are escaped.
 */
export function csvDataToString(data: string[][]): string {
  return data
    .map((row) => row.map((v) => `"${v.replaceAll('"', '""')}"`).join(',') + '\n')
    .join('');
}

export const CSV_MIME_TYPE = 'text/csv;charset=utf-8;';

/**
 * Formats a 2D array of strings as CSV and wraps it in a Blob, so callers that
 * save a CSV file share one MIME type.
 */
export function csvDataToBlob(data: string[][]): Blob {
  return new Blob([csvDataToString(data)], { type: CSV_MIME_TYPE });
}

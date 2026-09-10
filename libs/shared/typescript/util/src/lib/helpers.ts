/**
 * Utility functions for common operations across applications
 */

/**
 * Checks if a URL is an external link (starts with http/https)
 * @param url - The URL to check
 * @returns true if the URL is external, false otherwise
 */
export function isExternalLink(url: string): boolean {
  if (typeof url === 'string' && url.trim().startsWith('http')) {
    return true;
  }
  return false;
}

/**
 * Generates a random integer between min (inclusive) and max (exclusive)
 * @param minInclusive - Minimum value (inclusive)
 * @param maxExclusive - Maximum value (exclusive)
 * @returns Random integer
 */
export function getRandomInt(minInclusive: number, maxExclusive: number): number {
  minInclusive = Math.ceil(minInclusive);
  maxExclusive = Math.floor(maxExclusive);
  return Math.floor(Math.random() * (maxExclusive - minInclusive)) + minInclusive;
}

/**
 * Escapes regex special characters to prevent injection attacks
 * @param input - The string to escape
 * @returns String with regex characters escaped
 */
export function escapeRegexChars(input: string): string {
  return input.replace(/[-[\]{}()*+?.,\\^$|#\s]/g, '\\$&');
}

/**
 * Removes parentheses from a string
 * @param s - The string to process
 * @returns String with parentheses removed
 */
export function removeParentheses(s: string): string {
  return s.replace('(', '').replace(')', '');
}

/**
 * Converts a string to kebab-case
 * @param s - The string to convert
 * @returns String in kebab-case format
 */
export function toKebabCase(s: string): string {
  return s.toLowerCase().replace(/\s+/g, '-');
}

/**
 * Selects the singular or plural form of a word based on a count
 * @param singular - The singular form of the word
 * @param count - The number of items being described
 * @param plural - The plural form, defaulting to the singular with an appended "s"
 * @returns The singular form when count is exactly 1, otherwise the plural form
 */
export function pluralize(singular: string, count: number, plural = `${singular}s`): string {
  return count === 1 ? singular : plural;
}

/**
 * Capitalizes the first letter of a string, leaving the rest unchanged
 * @param value - The string to capitalize
 * @returns The string with its first letter uppercased, or an empty string for nullish/empty input
 */
export function capitalizeFirstLetter(value: string | null | undefined): string {
  if (!value) return '';
  return value.charAt(0).toUpperCase() + value.slice(1);
}

/**
 * Parses a multi-value query param written as a comma-separated list of individually
 * percent-encoded values, the inverse of `values.map(encodeURIComponent).join(',')`. Encoding each
 * value before joining is what lets a value contain a comma of its own, so splitting on commas
 * without decoding each entry is always wrong.
 *
 * Compare parsed arrays rather than raw param strings: the string the app writes for a set of values
 * is not the string another producer writes for the same values.
 * @param value - The param value as delivered by a URL parser (Angular's `parseUrl` or
 * `URL.searchParams`), which has already percent-decoded it once
 * @returns The decoded values in order, with blank entries dropped
 */
export function parseCommaSeparatedQueryParam(
  value: string | string[] | null | undefined,
): string[] {
  if (value == null) {
    return [];
  }

  const values = Array.isArray(value) ? value : [value];

  return values
    .flatMap((entry) => `${entry}`.split(','))
    .map((entry) => entry.trim())
    .map((entry) => {
      try {
        return decodeURIComponent(entry);
      } catch {
        return entry;
      }
    })
    .filter((entry) => entry.length > 0);
}

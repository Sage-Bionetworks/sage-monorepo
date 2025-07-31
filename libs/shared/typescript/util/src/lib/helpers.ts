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
 * Removes parentheses from a string
 * @param s - The string to process
 * @returns String with parentheses removed
 */
export function removeParenthesis(s: string): string {
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

/**
 * Escapes regex special characters so a caller-supplied string can be embedded in a pattern.
 *
 * Duplicated from `@sagebionetworks/shared/util` because that barrel also exports `SeoService`,
 * and importing an Angular service into a Playwright spec fails with a JIT compilation error.
 *
 * @param input - The string to escape
 * @returns String with regex characters escaped
 */
export function escapeRegexChars(input: string): string {
  return input.replace(/[-[\]{}()*+?.,\\^$|#\s]/g, String.raw`\$&`);
}

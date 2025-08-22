export function escapeRegexChars(input: string): string {
  // Escape all regex special characters to prevent injection
  return input.replace(/[-[\]{}()*+?.,\\^$|#\s]/g, '\\$&');
}

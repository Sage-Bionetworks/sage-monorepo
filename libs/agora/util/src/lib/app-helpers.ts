export function removeParenthesis(s: string) {
  return s.replace('(', '').replace(')', '');
}

export function formatGitTag(appVersion: string) {
  // remove the -rcX suffix
  return appVersion.replace(/-rc\d+$/, '');
}

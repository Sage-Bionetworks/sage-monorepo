export function removeParenthesis(s: string) {
  return s.replace('(', '').replace(')', '');
}

export function formatAppVersion(appVersion: string) {
  // remove the -rcX suffix
  return appVersion.replace(/-rc\d+$/, '');
}

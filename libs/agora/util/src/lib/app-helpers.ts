export function formatAppVersion(appVersion: string) {
  // remove the -rcX suffix
  return appVersion.replace(/-rc\d+$/, '');
}

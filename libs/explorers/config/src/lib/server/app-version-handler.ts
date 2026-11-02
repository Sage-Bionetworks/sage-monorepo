import type { Request, Response } from 'express';
import { AppVersion } from '../app-version';

type EnvironmentVariables = Record<string, string | undefined>;

/**
 * Reads the version the infra deploy injects into the app container. Empty strings mean the
 * container was started without them (e.g. local development), so the version is unknown.
 *
 * Must resolve the same values the app config resolves for `appVersion` and `commitSha` from these
 * environment variables, since the browser compares the two. Config file defaults are deliberately
 * not applied: without the environment variables the deployed version is unknown, and an unknown
 * version never counts as a mismatch.
 */
export function getDeployedAppVersion(environmentVariables: EnvironmentVariables): AppVersion {
  return {
    appVersion: environmentVariables['APP_VERSION'] ?? '',
    commitSha: environmentVariables['COMMIT_SHA'] ?? '',
  };
}

/**
 * Serves the deployed version so a browser tab can tell whether it is running code from an
 * earlier deploy. The response must never be cached, or a stale tab would keep reading the
 * version it was loaded with.
 */
export function createAppVersionHandler(environmentVariables: EnvironmentVariables) {
  const deployedAppVersion = getDeployedAppVersion(environmentVariables);
  return (_req: Request, res: Response) => {
    res.set('Cache-Control', 'no-store').json(deployedAppVersion);
  };
}

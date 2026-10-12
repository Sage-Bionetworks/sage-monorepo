import type { Request, Response } from 'express';

export interface AppVersion {
  appVersion: string;
  commitSha: string;
}

type Environment = Record<string, string | undefined>;

/**
 * Reads the version the infra deploy injects into the app container. Empty strings mean the
 * container was started without them (e.g. local development), so the version is unknown.
 */
export function getDeployedAppVersion(env: Environment): AppVersion {
  return {
    appVersion: env['APP_VERSION'] ?? '',
    commitSha: env['COMMIT_SHA'] ?? '',
  };
}

/**
 * Serves the deployed version so a browser tab can tell whether it is running code from an
 * earlier deploy. The response must never be cached, or a stale tab would keep reading the
 * version it was loaded with.
 */
export function createAppVersionHandler(env: Environment) {
  const deployedAppVersion = getDeployedAppVersion(env);
  return (_req: Request, res: Response) => {
    res.set('Cache-Control', 'no-store').json(deployedAppVersion);
  };
}

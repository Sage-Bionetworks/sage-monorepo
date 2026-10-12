import type { Request, Response } from 'express';
import { createAppVersionHandler, getDeployedAppVersion } from './app-version';

const deployedEnv = { APP_VERSION: '4.0.0-rc3', COMMIT_SHA: 'abc1234' };

describe('getDeployedAppVersion', () => {
  it('returns the version injected by the deploy', () => {
    expect(getDeployedAppVersion(deployedEnv)).toEqual({
      appVersion: deployedEnv.APP_VERSION,
      commitSha: deployedEnv.COMMIT_SHA,
    });
  });

  it('returns empty strings when the deploy injected no version', () => {
    expect(getDeployedAppVersion({})).toEqual({ appVersion: '', commitSha: '' });
  });
});

describe('createAppVersionHandler', () => {
  it('responds with the deployed version and forbids caching', () => {
    const res = { set: jest.fn(), json: jest.fn() };
    res.set.mockReturnValue(res);

    createAppVersionHandler(deployedEnv)({} as Request, res as unknown as Response);

    expect(res.set).toHaveBeenCalledWith('Cache-Control', 'no-store');
    expect(res.json).toHaveBeenCalledWith(getDeployedAppVersion(deployedEnv));
  });
});

import { PLATFORM_ID } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { EnvironmentMapperService } from '@sagebionetworks/platform/config/angular';
import type { Request, Response } from 'express';
import { AppVersion, createAppVersionHandler, getDeployedAppVersion } from './app-version';

const deployedEnvironmentVariables = { APP_VERSION: '4.0.0-rc3', COMMIT_SHA: 'abc1234' };
/** Mirrors the version keys in the apps' `application.yaml`. */
const configFileVersion: AppVersion = { appVersion: 'local', commitSha: '' };

describe('getDeployedAppVersion', () => {
  it('returns the version injected by the deploy', () => {
    expect(getDeployedAppVersion(deployedEnvironmentVariables)).toEqual({
      appVersion: deployedEnvironmentVariables.APP_VERSION,
      commitSha: deployedEnvironmentVariables.COMMIT_SHA,
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

    createAppVersionHandler(deployedEnvironmentVariables)(
      {} as Request,
      res as unknown as Response,
    );

    expect(res.set).toHaveBeenCalledWith('Cache-Control', 'no-store');
    expect(res.json).toHaveBeenCalledWith(getDeployedAppVersion(deployedEnvironmentVariables));
  });
});

describe('getDeployedAppVersion and the app config', () => {
  it('resolve the same version from the deploy environment variables', () => {
    TestBed.configureTestingModule({ providers: [{ provide: PLATFORM_ID, useValue: 'server' }] });
    jest.replaceProperty(process, 'env', { ...process.env, ...deployedEnvironmentVariables });

    const appConfigVersion =
      TestBed.inject(EnvironmentMapperService).applyEnvironmentOverrides(configFileVersion);

    expect(appConfigVersion).toEqual(getDeployedAppVersion(process.env));
  });
});

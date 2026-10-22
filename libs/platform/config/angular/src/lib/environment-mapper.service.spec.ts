import { PLATFORM_ID } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { EnvironmentMapperService } from './environment-mapper.service';

const allDigitCommitSha = '1234567';
const leadingZeroCommitSha = '0123456';

describe('EnvironmentMapperService', () => {
  function setup(platformId: 'server' | 'browser' = 'server'): EnvironmentMapperService {
    TestBed.configureTestingModule({ providers: [{ provide: PLATFORM_ID, useValue: platformId }] });
    return TestBed.inject(EnvironmentMapperService);
  }

  function setEnvironmentVariables(environmentVariables: Record<string, string>) {
    jest.replaceProperty(process, 'env', { ...process.env, ...environmentVariables });
  }

  afterEach(() => {
    jest.restoreAllMocks();
    TestBed.resetTestingModule();
  });

  describe('when the config file value is a string', () => {
    it.each([allDigitCommitSha, leadingZeroCommitSha, 'true', 'null', '{"a":1}'])(
      'keeps the override %s as a string',
      (commitSha) => {
        setEnvironmentVariables({ COMMIT_SHA: commitSha });

        expect(setup().applyEnvironmentOverrides({ commitSha: '' })).toEqual({ commitSha });
      },
    );
  });

  describe('when the config file value is not a string', () => {
    it('parses a boolean override', () => {
      setEnvironmentVariables({ GOOGLE_TAG_MANAGER_ENABLED: 'false' });

      expect(setup().applyEnvironmentOverrides({ googleTagManagerEnabled: true })).toEqual({
        googleTagManagerEnabled: false,
      });
    });

    it('parses a number override in a nested value', () => {
      setEnvironmentVariables({ BATTLE_PROMPT_LENGTH_LIMIT: '3000' });

      expect(setup().applyEnvironmentOverrides({ battle: { promptLengthLimit: 5000 } })).toEqual({
        battle: { promptLengthLimit: 3000 },
      });
    });
  });

  it('reports overrides with the same types it applies', () => {
    setEnvironmentVariables({ COMMIT_SHA: allDigitCommitSha, GOOGLE_TAG_MANAGER_ENABLED: 'false' });

    expect(
      setup().getMatchingEnvironmentVariables({ commitSha: '', googleTagManagerEnabled: true }),
    ).toEqual({ commitSha: allDigitCommitSha, googleTagManagerEnabled: false });
  });

  it('applies no overrides in the browser', () => {
    setEnvironmentVariables({ COMMIT_SHA: allDigitCommitSha });

    expect(setup('browser').applyEnvironmentOverrides({ commitSha: '' })).toEqual({
      commitSha: '',
    });
  });
});

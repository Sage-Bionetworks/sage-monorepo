import { YamlParserService } from '@sagebionetworks/platform/config/angular';
import { load } from 'js-yaml';
import { existsSync, readFileSync } from 'node:fs';
import { join, resolve } from 'node:path';
import { assertValidAppConfig } from './startup-config';

const AGORA_CONFIG_DIRECTORY = resolve(__dirname, '../../../../../../apps/agora/app/src/config');
const TRACES_SAMPLE_RATE_ENV = 'SENTRY_TRACES_SAMPLE_RATE';

describe('assertValidAppConfig', () => {
  beforeEach(() => {
    jest.spyOn(console, 'log').mockImplementation();
    jest.spyOn(console, 'error').mockImplementation();
    // Jest can't run the loader's runtime `import('node:fs/promises')`, so the files it would read
    // are read here instead; everything after reading them is the real loader.
    jest
      .spyOn(YamlParserService.prototype, 'loadYaml')
      .mockImplementation(async (filename, basePath = '') => {
        const filePath = join(basePath, filename);
        return existsSync(filePath)
          ? ((load(readFileSync(filePath, 'utf-8')) as Record<string, unknown>) ?? {})
          : null;
      });
  });

  afterEach(() => {
    delete process.env[TRACES_SAMPLE_RATE_ENV];
    jest.restoreAllMocks();
  });

  it('resolves for the config the build ships', async () => {
    await expect(assertValidAppConfig(AGORA_CONFIG_DIRECTORY)).resolves.toBeUndefined();
  });

  it('resolves for a valid value set through the environment', async () => {
    process.env[TRACES_SAMPLE_RATE_ENV] = '0.25';

    await expect(assertValidAppConfig(AGORA_CONFIG_DIRECTORY)).resolves.toBeUndefined();
  });

  it('rejects an invalid value set through the environment', async () => {
    process.env[TRACES_SAMPLE_RATE_ENV] = '10%';

    await expect(assertValidAppConfig(AGORA_CONFIG_DIRECTORY)).rejects.toThrow(
      /sentryTracesSampleRate/,
    );
  });

  it('rejects when the config directory has no base config', async () => {
    await expect(assertValidAppConfig(join(AGORA_CONFIG_DIRECTORY, 'missing'))).rejects.toThrow(
      /application\.yaml/,
    );
  });
});

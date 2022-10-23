import { Configuration } from './configuration';
import { Context } from 'probot';
import { ajv, configSchema } from '../schema';
import { RepoConfiguration } from './repo-configuration';
import { InvalidConfigError } from '../error';
import deepmerge from 'deepmerge';

const CONFIG_FILE = 'challenge-bot.yml';
const DEFAULT_MESSAGE = 'This is the default message.';

export const DEFAULT_CONFIGURATION: Configuration = {
  message: DEFAULT_MESSAGE,
};

export const getRepoConfiguration = async (
  context: Context<'issues'>
): Promise<Configuration> => {
  const validate = ajv.compile(configSchema);

  const repoConfig = (await context.config(CONFIG_FILE)) as RepoConfiguration;

  if (repoConfig) {
    if (!validate(repoConfig)) {
      throw new InvalidConfigError(
        'configuration is invalid: ' + JSON.stringify(validate.errors, null, 2)
      );
    }

    return deepmerge(DEFAULT_CONFIGURATION, repoConfig as Configuration, {
      // overwrite from repo config
      arrayMerge: (_, b) => b,
    });
  }

  return DEFAULT_CONFIGURATION;
};

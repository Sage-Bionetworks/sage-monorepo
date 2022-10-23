import { getConfiguration } from '../src/config';
import fs from 'fs';
import yaml from 'js-yaml';
import { Context } from 'probot';

const fakeContext = {
  config: async () =>
    yaml.load(fs.readFileSync('./fixtures/challenge-bot.yml', 'utf8')),
} as unknown as Context<'issues'>;

test('should override default rules', async () => {
  const config = await getConfiguration(fakeContext);
  expect(config).toEqual({
    message: 'This is an awesome message.',
  });
});

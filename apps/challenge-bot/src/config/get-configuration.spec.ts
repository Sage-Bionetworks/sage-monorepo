import { getConfiguration } from '.';
import fs from 'fs';
import yaml from 'js-yaml';
import { Context } from 'probot';
import path from 'path';

const fakeContext = {
  config: async () =>
    yaml.load(
      fs.readFileSync(
        path.join(__dirname, '../../test/fixtures/challenge-bot.yml'),
        'utf8'
      )
    ),
} as unknown as Context<'issues'>;

test('should override default rules', async () => {
  const config = await getConfiguration(fakeContext);
  expect(config).toEqual({
    message: 'This is an awesome message.',
  });
});

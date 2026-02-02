import type { StorybookConfig } from '@storybook/angular';
import { createRequire } from 'node:module';
import { dirname, join } from 'node:path';

const require = createRequire(import.meta.url);

// Use relative paths for production builds, localhost URLs for dev mode
const isProductionBuild = process.env['NODE_ENV'] === 'production';

const config: StorybookConfig = {
  stories: ['../src/**/*.stories.@(js|jsx|ts|tsx|mdx)'],
  addons: [getAbsolutePath('@storybook/addon-docs')],
  framework: {
    name: getAbsolutePath('@storybook/angular'),
    options: {},
  },
  refs: isProductionBuild
    ? {
        agora: {
          title: 'Agora',
          url: './agora',
        },
        explorers: {
          title: 'Explorers',
          url: './explorers',
        },
      }
    : {
        agora: {
          title: 'Agora',
          url: 'http://localhost:4401',
        },
        explorers: {
          title: 'Explorers',
          url: 'http://localhost:4402',
        },
      },
};

export default config;

function getAbsolutePath(value: string): any {
  return dirname(require.resolve(join(value, 'package.json')));
}

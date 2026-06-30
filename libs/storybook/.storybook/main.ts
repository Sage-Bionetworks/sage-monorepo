import type { StorybookConfig } from '@storybook/angular';
import { createRequire } from 'node:module';
import { dirname, join } from 'node:path';

const require = createRequire(import.meta.url);

// Use relative paths for static builds, localhost URLs for dev mode
const isStaticStorybookBuild = process.env['STORYBOOK_STATIC_BUILD'] === 'true';

const config: StorybookConfig = {
  stories: ['../src/**/*.stories.@(js|jsx|ts|tsx|mdx)'],
  addons: [getAbsolutePath('@storybook/addon-docs')],
  framework: {
    name: getAbsolutePath('@storybook/angular'),
    options: {},
  },
  refs: isStaticStorybookBuild
    ? {
        agora: {
          title: 'Agora',
          url: './agora',
        },
        explorers: {
          title: 'Explorers',
          url: './explorers',
        },
        qtl: {
          title: 'QTL',
          url: './qtl',
        },
        'model-ad': {
          title: 'Model AD',
          url: './model-ad',
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
        qtl: {
          title: 'QTL',
          url: 'http://localhost:4403',
        },
        'model-ad': {
          title: 'Model AD',
          url: 'http://localhost:4404',
        },
      },
};

export default config;

function getAbsolutePath(value: string): any {
  return dirname(require.resolve(join(value, 'package.json')));
}

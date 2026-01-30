import { createRequire } from 'node:module';
import { dirname, join } from 'node:path';
import type { StorybookConfig } from '@storybook/angular';

const require = createRequire(import.meta.url);

/**
 * IMPORTANT: When adding stories to a new library, you must add that library's
 * project name to the "implicitDependencies" array in libs/storybook/project.json.
 * This ensures Nx properly invalidates the Storybook cache when that library changes.
 *
 * Example: If you add stories to "agora-teams", add "agora-teams" to the array.
 */

const config: StorybookConfig = {
  stories: ['../../**/src/lib/**/*.stories.@(js|jsx|ts|tsx|mdx)'],
  addons: [getAbsolutePath('@storybook/addon-docs')],
  framework: {
    name: getAbsolutePath('@storybook/angular'),
    options: {},
  },
};

export default config;

function getAbsolutePath(value: string): any {
  return dirname(require.resolve(join(value, 'package.json')));
}

// To customize your webpack configuration you can use the webpackFinal field.
// Check https://storybook.js.org/docs/react/builders/webpack#extending-storybooks-webpack-config
// and https://nx.dev/recipes/storybook/custom-builder-configs

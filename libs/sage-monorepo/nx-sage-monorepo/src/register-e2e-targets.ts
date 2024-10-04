import { TargetConfiguration } from 'nx/src/config/workspace-json-project-json';
import { memoize } from './utils/memoize-utils';
import { getProjectRoot } from './utils/project-utils';

export function registerE2eTargets(projectPath: string): Record<string, TargetConfiguration> {
  return generateTargetsMemoized(projectPath);
}

export const generateE2eTargets = (projectPath: string): Record<string, TargetConfiguration> => {
  const projectRoot = getProjectRoot(projectPath);

  return {
    e2e: generateE2eTarget(projectRoot),
    lint: generateLintTarget(projectRoot),
  };
};

const generateTargetsMemoized = memoize(generateE2eTargets);

const generateE2eTarget = (projectRoot: string): TargetConfiguration => ({
  executor: '@nx/cypress:cypress',
  options: {
    cypressConfig: `${projectRoot}/cypress.config.ts`,
    devServerTarget: 'my-app:serve:development',
    testingType: 'e2e',
  },
  configurations: {
    production: {
      devServerTarget: 'my-app:serve:production',
    },
  },
});

const generateLintTarget = (projectRoot: string): TargetConfiguration => ({
  executor: '@nx/linter:eslint',
  outputs: ['{options.outputFile}'],
  options: {
    lintFilePatterns: [`${projectRoot}/**/*.{js,ts}`],
  },
});

import { TargetConfiguration } from '@nx/devkit';

export const projectFilePatterns = ['project.json'];

export function registerProjectTargets(): Record<string, TargetConfiguration> {
  return {
    'build-image': {
      executor: '@nx-tools/nx-container:build',
      outputs: [],
      defaultConfiguration: 'local',
      options: {
        context: '{projectRoot}',
      },
      configurations: {
        local: {
          metadata: {
            images: ['ghcr.io/sage-bionetworks/{projectName}'],
            tags: ['type=edge,branch=main', 'type=raw,value=local', 'type=sha'],
          },
        },
        ci: {
          metadata: {
            images: ['ghcr.io/sage-bionetworks/{projectName}'],
            tags: ['type=semver,pattern={{version}},value=${VERSION}', 'type=sha'],
          },
          push: true,
        },
      },
    },
  };
}

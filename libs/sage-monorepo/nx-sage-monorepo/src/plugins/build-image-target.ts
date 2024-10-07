import { TargetConfiguration } from '@nx/devkit';

export async function buildImageTarget(
  projectRoot: string,
  projectName: string,
): Promise<TargetConfiguration> {
  return {
    executor: '@nx-tools/nx-container:build',
    outputs: [],

    options: {
      context: projectRoot,
    },
    cache: false,
    configurations: {
      local: {
        metadata: {
          images: [`ghcr.io/sage-bionetworks/${projectName}`],
          tags: ['type=edge,branch=main', 'type=raw,value=local', 'type=sha'],
        },
      },
      ci: {
        metadata: {
          images: [`ghcr.io/sage-bionetworks/${projectName}`],
          tags: ['type=semver,pattern={{version}},value=${VERSION}', 'type=sha'],
        },
        push: true,
      },
    },
    defaultConfiguration: 'local',
    dependsOn: ['build'],
  };
}

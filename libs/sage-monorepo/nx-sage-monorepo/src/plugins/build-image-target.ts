import { TargetConfiguration } from '@nx/devkit';
import { Builder } from './project-metadata';

export async function buildImageTarget(
  projectRoot: string,
  projectName: string,
  projectBuilder: Builder | undefined | null, // TODO: builder could be app or image, be more specific
): Promise<TargetConfiguration> {
  const dependsOn = [];
  if (projectBuilder === 'gradle') {
    dependsOn.push({
      target: 'build-image-base',
    });
  } else if (projectBuilder === 'webpack') {
    dependsOn.push({
      // TODO: the task `server` is more about Angular that the build itself. To revisit. Also,
      // shall we let the user decide between CSR and SSR?
      target: 'server',
    });
  } else {
    dependsOn.push({
      target: 'build',
    });
  }

  let context = projectRoot;
  // TODO: The context must be set to '.' for Angular app. Be more specific.
  if (projectBuilder === 'webpack') {
    context = '.';
  }

  return {
    executor: '@nx-tools/nx-container:build',
    outputs: [],

    options: {
      context,
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
    dependsOn,
  };
}

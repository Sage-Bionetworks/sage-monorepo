import { TargetConfiguration } from '@nx/devkit';
import { Builder, Framework, ContainerImageType } from './project-metadata';

export async function buildImageTarget(
  projectRoot: string,
  projectName: string,
  projectBuilder: Builder | undefined | null, // TODO: builder could be app or image, be more specific
  projectFramework: Framework | null,
  containerImageType: ContainerImageType | null,
): Promise<TargetConfiguration> {
  const dependsOn = [];

  // Add container image template generation if needed
  if (containerImageType && containerImageType !== 'custom') {
    dependsOn.push({
      target: 'generate-dockerfile',
    });
  }

  if (projectBuilder === 'gradle') {
    dependsOn.push({
      target: 'build-image-base',
    });
    // This dependency is required for Java apps build with GraalVM.
    // dependsOn.push({
    //   target: 'build',
    // });
  } else if (projectBuilder === 'webpack' && projectFramework === 'angular') {
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
  // Actually, this is also valid for `agora-api` built with Webpack.
  if (projectBuilder === 'webpack') {
    context = '.';
  }

  // Determine which Dockerfile to use
  let dockerfile = 'Dockerfile';
  if (containerImageType && containerImageType !== 'custom') {
    dockerfile = 'Dockerfile.generated';
  }

  return {
    executor: '@nx-tools/nx-container:build',
    outputs: [],
    options: {
      context,
      file: `${context}/${dockerfile}`,
    },
    cache: false,
    configurations: {
      local: {
        metadata: {
          images: [`ghcr.io/sage-bionetworks/${projectName}`],
          tags: ['type=raw,value=local', 'type=sha'],
        },
      },
      edge: {
        metadata: {
          images: [`ghcr.io/sage-bionetworks/${projectName}`],
          tags: ['type=edge,branch=main', 'type=sha'],
        },
        push: true,
      },
      release: {
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

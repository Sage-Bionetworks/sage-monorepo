import { TargetConfiguration } from '@nx/devkit';
import { BaseImageType } from './project-metadata';
import { getBaseImageString } from '../config/base-images';

export function generateDockerfileTarget(
  projectRoot: string,
  baseImageType: BaseImageType,
): TargetConfiguration {
  if (baseImageType === 'custom') {
    throw new Error('Cannot generate Dockerfile for custom base image type');
  }

  const baseImage = getBaseImageString(baseImageType);

  // Special handling for apex projects to determine health check port
  let healthCheckPort = '8000'; // default
  if (baseImageType === 'apex') {
    if (projectRoot.includes('amp-als')) {
      healthCheckPort = '8400';
    } else if (projectRoot.includes('openchallenges')) {
      healthCheckPort = '8000';
    }
  }

  const templatePath = `libs/sage-monorepo/nx-plugin/src/templates/${baseImageType}.Dockerfile.template`;

  return {
    executor: 'nx:run-commands',
    options: {
      commands: [
        {
          command: `echo "Generating Dockerfile with base image: ${baseImage}"`,
        },
        {
          command: `sed 's|{{baseImage}}|${baseImage}|g; s|{{healthCheckPort}}|${healthCheckPort}|g' ${templatePath} > ${projectRoot}/Dockerfile.generated`,
        },
      ],
    },
  };
}

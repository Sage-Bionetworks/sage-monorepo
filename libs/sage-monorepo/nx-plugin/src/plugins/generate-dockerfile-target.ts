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

  const templatePath = `libs/sage-monorepo/nx-plugin/src/templates/${baseImageType}.Dockerfile.template`;

  return {
    executor: 'nx:run-commands',
    options: {
      commands: [
        {
          command: `echo "Generating Dockerfile with base image: ${baseImage}"`,
        },
        {
          command: `sed 's|{{baseImage}}|${baseImage}|g' ${templatePath} > ${projectRoot}/Dockerfile.generated`,
        },
      ],
    },
  };
}

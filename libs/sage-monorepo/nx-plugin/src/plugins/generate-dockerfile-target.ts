import { TargetConfiguration } from '@nx/devkit';
import { ContainerImageType } from './project-metadata';
import { getContainerImageString } from '../config/container-images';

export function generateDockerfileTarget(
  projectRoot: string,
  containerImageType: ContainerImageType,
): TargetConfiguration {
  if (containerImageType === 'custom') {
    throw new Error('Cannot generate Dockerfile for custom container image type');
  }

  const baseImage = getContainerImageString(containerImageType);

  const templatePath = `libs/sage-monorepo/nx-plugin/src/templates/${containerImageType}.Dockerfile.template`;

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

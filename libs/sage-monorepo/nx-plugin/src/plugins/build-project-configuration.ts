import { TargetConfiguration } from '@nx/devkit';
import { buildImageTarget } from './build-image-target';
import { generateDockerfileTarget } from './generate-dockerfile-target';
import { SageMonorepoProjectConfiguration } from './project-configuration';
import { ProjectConfigurationBuilderOptions } from './project-configuration-builder-options';
import { BASE_IMAGES } from '../config/base-images';

function createValidationErrorTarget(projectName: string, invalidTag: string): TargetConfiguration {
  const supportedImageTypes = Object.keys(BASE_IMAGES);
  const validValues = [...supportedImageTypes, 'custom']
    .map((type) => `container-image:${type}`)
    .join(', ');

  return {
    executor: 'nx:run-commands',
    options: {
      command: `echo "❌ Invalid container-image tag '${invalidTag}' in project ${projectName}. Supported values: ${validValues}. Please update the project.json tags." && exit 1`,
    },
  };
}

function validateContainerImageTag(options: ProjectConfigurationBuilderOptions): string | null {
  const tags = options.projectConfiguration.tags || [];
  const containerImageTag = tags.find((tag) => tag.startsWith('container-image:'));

  if (containerImageTag) {
    const imageType = containerImageTag.split(':')[1];
    const supportedImageTypes = Object.keys(BASE_IMAGES);
    const validTypes = [...supportedImageTypes, 'custom'];

    if (!validTypes.includes(imageType)) {
      return containerImageTag; // Return the invalid tag
    }
  }

  return null; // Valid or no container-image tag
}

export async function buildProjectConfiguration(
  options: ProjectConfigurationBuilderOptions,
): Promise<SageMonorepoProjectConfiguration> {
  const targets: Record<string, TargetConfiguration> = {};

  const pluginConfig = options.pluginConfig;

  if (options.projectMetadata.containerType === 'docker') {
    // Check for invalid container-image tag first
    const invalidTag = validateContainerImageTag(options);
    if (invalidTag) {
      // Create an error target that will fail with helpful message
      targets['generate-dockerfile'] = createValidationErrorTarget(options.projectName, invalidTag);
      targets[pluginConfig.buildImageTargetName] = createValidationErrorTarget(
        options.projectName,
        invalidTag,
      );
    } else {
      // Add dockerfile generation target if using a standard base image
      if (
        options.projectMetadata.baseImageType &&
        options.projectMetadata.baseImageType !== 'custom'
      ) {
        targets['generate-dockerfile'] = generateDockerfileTarget(
          options.projectRoot,
          options.projectMetadata.baseImageType,
        );
      }

      targets[pluginConfig.buildImageTargetName] = await buildImageTarget(
        options.projectRoot,
        options.projectName,
        options.projectMetadata.builder,
        options.projectMetadata.framework,
        options.projectMetadata.baseImageType,
      );
    }
  }

  const metadata = {};
  const tags: string[] = [];
  if (options.projectMetadata.builder) {
    tags.push(`builder:${options.projectMetadata.builder}`);
  }

  return { targets, metadata, tags };
}

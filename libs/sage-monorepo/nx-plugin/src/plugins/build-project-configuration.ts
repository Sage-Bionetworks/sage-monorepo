import { TargetConfiguration } from '@nx/devkit';
import { buildImageTarget } from './build-image-target';
import { generateDockerfileTarget } from './generate-dockerfile-target';
import { SageMonorepoProjectConfiguration } from './project-configuration';
import { ProjectConfigurationBuilderOptions } from './project-configuration-builder-options';

export async function buildProjectConfiguration(
  options: ProjectConfigurationBuilderOptions,
): Promise<SageMonorepoProjectConfiguration> {
  const targets: Record<string, TargetConfiguration> = {};

  const pluginConfig = options.pluginConfig;

  if (options.projectMetadata.containerType === 'docker') {
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

  const metadata = {};
  const tags: string[] = [];
  if (options.projectMetadata.builder) {
    tags.push(`builder:${options.projectMetadata.builder}`);
  }
  if (options.projectMetadata.baseImageType) {
    tags.push(`base-image:${options.projectMetadata.baseImageType}`);
  }

  return { targets, metadata, tags };
}

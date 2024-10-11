import { TargetConfiguration } from '@nx/devkit';
import { buildImageTarget } from './build-image-target';
import { SageMonorepoProjectConfiguration } from './project-configuration';
import { ProjectConfigurationBuilderOptions } from './project-configuration-builder-options';

export async function buildProjectConfiguration(
  options: ProjectConfigurationBuilderOptions,
): Promise<SageMonorepoProjectConfiguration> {
  const targets: Record<string, TargetConfiguration> = {};

  const pluginConfig = options.pluginConfig;

  if (options.dockerized) {
    targets[pluginConfig.buildImageTargetName] = await buildImageTarget(
      options.projectRoot,
      options.projectName,
      options.projectMetadata.builder,
    );
  }

  const metadata = {};
  const tags: string[] = [];
  if (options.projectMetadata.builder) {
    tags.push(`builder:${options.projectMetadata.builder}`);
  }

  return { targets, metadata, tags };
}

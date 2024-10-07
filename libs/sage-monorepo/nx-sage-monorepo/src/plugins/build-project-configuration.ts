import { TargetConfiguration } from '@nx/devkit';
import { buildImageTarget } from './build-image-target';
import { SageMonorepoProjectConfiguration } from './sage-monorepo-project-configuration';
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
      options.projectBuilder,
    );
  }

  const metadata = {};
  const tags: string[] = [];
  if (options.projectBuilder) {
    tags.push(`builder:${options.projectBuilder}`);
  }

  return { targets, metadata, tags };
}

import { TargetConfiguration } from '@nx/devkit';
import { buildImageTarget } from './build-image-target';
import { SageMonorepoProjectConfiguration } from './sage-monorepo-project-configuration';
import { ProjectConfigurationBuilderOptions } from './project-configuration-builder-options';

export async function buildProjectConfiguration(
  options: ProjectConfigurationBuilderOptions,
): Promise<SageMonorepoProjectConfiguration> {
  const targets: Record<string, TargetConfiguration> = {};

  const pluginConfig = options.pluginConfig;

  targets[pluginConfig.buildImageTargetName] = await buildImageTarget(options.projectRoot);

  const metadata = {};
  const tags: string[] = [];
  return { targets, metadata, tags };
}

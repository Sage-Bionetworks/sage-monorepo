import { TargetConfiguration } from '@nx/devkit';
import { buildImageTarget } from './build-image-target';
import { SageMonorepoProjectConfiguration } from './sage-monorepo-project-configuration';
import { SageMonorepoPluginConfiguration } from './sage-monorepo-plugin-configuration';

export async function buildProjectConfiguration(
  projectRoot: string,
  config: SageMonorepoPluginConfiguration,
): Promise<SageMonorepoProjectConfiguration> {
  const targets: Record<string, TargetConfiguration> = {};

  targets[config.buildImageTargetName] = await buildImageTarget(projectRoot);

  const metadata = {};
  return { targets, metadata };
}

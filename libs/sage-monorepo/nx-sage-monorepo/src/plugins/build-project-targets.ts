import { ProjectType, TargetConfiguration } from '@nx/devkit';
import { buildImageTarget } from './build-image-target';
import { SageMonorepoProjectConfiguration } from './sage-monorepo-project-configuration';
import { SageMonorepoPluginConfiguration } from './sage-monorepo-plugin-configuration';
import { inferProjectType } from './project-type';

export async function buildProjectTargets(
  projectRoot: string,
  config: SageMonorepoPluginConfiguration,
): Promise<SageMonorepoProjectConfiguration> {
  const targets: Record<string, TargetConfiguration> = {};

  // Define if it's an app and if it's dockerized
  const projectType = inferProjectType(projectRoot);
  console.log(`projectType: ${projectType}`);

  targets[config.buildImageTargetName] = await buildImageTarget(projectRoot);

  const metadata = {};
  return { targets, metadata };
}

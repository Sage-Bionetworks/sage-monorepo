import { SageMonorepoPluginOptions } from './sage-monorepo-plugin-options';

export interface SageMonorepoPluginConfiguration {
  buildImageTargetName: string;
}

export function createPluginConfiguration({
  buildImageTargetName = 'build-image',
}: SageMonorepoPluginOptions): SageMonorepoPluginConfiguration {
  return {
    buildImageTargetName,
  };
}

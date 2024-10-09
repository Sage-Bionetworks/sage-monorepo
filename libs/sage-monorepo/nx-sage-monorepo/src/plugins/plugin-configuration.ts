import { PluginOptions } from './plugin-options';

export type PluginConfiguration = {
  buildImageTargetName: string;
};

export function createPluginConfiguration({
  buildImageTargetName = 'build-image',
}: PluginOptions): PluginConfiguration {
  return {
    buildImageTargetName,
  };
}

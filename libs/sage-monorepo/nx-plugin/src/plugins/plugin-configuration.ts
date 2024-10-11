export type SageMonorepoPluginOptions = {
  buildImageTargetName?: string;
};

export type SageMonorepoPluginConfiguration = Required<SageMonorepoPluginOptions>;

export function createPluginConfiguration({
  buildImageTargetName = 'build-image',
}: SageMonorepoPluginOptions): SageMonorepoPluginConfiguration {
  return {
    buildImageTargetName,
  };
}

import { ProjectConfiguration } from '@nx/devkit';
import { SageMonorepoPluginConfiguration } from './plugin-configuration';
import { ProjectMetadata } from './project-metadata';

export type ProjectConfigurationBuilderOptions = {
  projectRoot: string;
  projectName: string;
  pluginConfig: SageMonorepoPluginConfiguration;
  projectMetadata: ProjectMetadata;
  projectConfiguration: ProjectConfiguration;
};

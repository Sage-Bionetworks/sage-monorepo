import { PluginConfiguration } from './plugin-configuration';
import { ProjectMetadata } from './project-metadata';

export type ProjectConfigurationBuilderOptions = {
  projectRoot: string;
  projectName: string;
  pluginConfig: PluginConfiguration;
  projectMetadata: ProjectMetadata;
  dockerized?: boolean;
};

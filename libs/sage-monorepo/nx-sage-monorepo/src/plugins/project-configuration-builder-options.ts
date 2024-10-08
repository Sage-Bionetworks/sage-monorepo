import { PluginConfiguration } from './plugin-configuration';
import { ProjectBuilder } from './project-builder';

export type ProjectConfigurationBuilderOptions = {
  projectRoot: string;
  projectName: string;
  pluginConfig: PluginConfiguration;
  projectBuilder?: ProjectBuilder;
  dockerized?: boolean;
};

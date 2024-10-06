import { PluginConfiguration } from './plugin-configuration';
import { ProjectBuilder } from './project-builder';

export type ProjectConfigurationBuilderOptions = {
  projectRoot: string;
  pluginConfig: PluginConfiguration;
  projectBuilder?: ProjectBuilder;
};

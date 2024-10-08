import {
  CreateNodesContext,
  createNodesFromFiles,
  CreateNodesV2,
  detectPackageManager,
  ProjectConfiguration,
  readJsonFile,
  writeJsonFile,
} from '@nx/devkit';
import { hashObject } from 'nx/src/hasher/file-hasher';
import { workspaceDataDirectory } from 'nx/src/utils/cache-directory';
import { calculateHashForCreateNodes } from '@nx/devkit/src/utils/calculate-hash-for-create-nodes';
import { dirname, join } from 'path';
import { existsSync, readdirSync } from 'fs';
import { getLockFileName } from '@nx/js';
import { SageMonorepoProjectConfiguration } from './sage-monorepo-project-configuration';
import { createPluginConfiguration } from './plugin-configuration';
import { PluginOptions } from './plugin-options';
import { buildProjectConfiguration } from './build-project-configuration';
import { inferProjectType } from './project-type';
import { inferProjectBuilder } from './project-builder';
import { ProjectConfigurationBuilderOptions } from './project-configuration-builder-options';

function readProjectCOnfigurationsCache(
  cachePath: string,
): Record<string, SageMonorepoProjectConfiguration> {
  console.log(`cachePath: ${cachePath}`);
  return existsSync(cachePath) ? readJsonFile(cachePath) : {};
}

function writeProjectConfigurationsToCache(
  cachePath: string,
  results: Record<string, SageMonorepoProjectConfiguration>,
) {
  writeJsonFile(cachePath, results);
}

const projectFilePattern = '{apps,libs}/openchallenges/**/project.json';

export const createNodesV2: CreateNodesV2<PluginOptions> = [
  projectFilePattern,
  async (configFilePaths, options, context) => {
    options ??= {};
    const optionsHash = hashObject(options);

    // Reads the cached targets for all the projects
    const cachePath = join(workspaceDataDirectory, `sage-monorepo-${optionsHash}.hash`);
    const projectConfigurationsCache = readProjectCOnfigurationsCache(cachePath);
    try {
      return await createNodesFromFiles(
        (configFile, options, context) => {
          return createNodesInternal(configFile, options, context, projectConfigurationsCache);
        },
        configFilePaths,
        options,
        context,
      );
    } finally {
      writeProjectConfigurationsToCache(cachePath, projectConfigurationsCache);
    }
  },
];

async function createNodesInternal(
  configFilePath: string,
  options: PluginOptions | undefined,
  context: CreateNodesContext,
  projectConfigurationsCache: Record<string, SageMonorepoProjectConfiguration>,
) {
  const projectRoot = dirname(configFilePath);
  const siblingFiles = readdirSync(join(context.workspaceRoot, projectRoot));

  // Content of the project file
  const projectFileContent: ProjectConfiguration = readJsonFile(configFilePath);

  const projectName = projectFileContent.name;
  if (typeof projectName !== 'string') {
    throw new Error('Project name is undefined or not a valid string.');
  }
  console.log(`projectName: ${projectName}`);

  const projectType = inferProjectType(projectRoot);
  console.log(`projectType: ${projectType}`);

  const dockerized = projectType === 'application' && siblingFiles.includes('Dockerfile');
  console.log(`dockerized: ${dockerized}`);

  const projectBuilder = inferProjectBuilder(context.workspaceRoot, projectRoot);
  console.log(`projectBuilder: ${projectBuilder}`);

  const pluginConfig = createPluginConfiguration(options || {});

  // We do not want to alter how the hash is calculated, so appending the config file path to the
  // hash to prevent the project files overwriting the target cache created by the other project
  const hash =
    (await calculateHashForCreateNodes(projectRoot, pluginConfig, context, [
      getLockFileName(detectPackageManager(context.workspaceRoot)),
    ])) + configFilePath;

  const projectConfigurationBuilderOptions: ProjectConfigurationBuilderOptions = {
    projectRoot,
    projectName,
    pluginConfig,
    projectBuilder,
    dockerized,
  };
  projectConfigurationsCache[hash] ??= await buildProjectConfiguration(
    projectConfigurationBuilderOptions,
  );

  const { targets, metadata, tags } = projectConfigurationsCache[hash];
  const project: ProjectConfiguration = {
    root: projectRoot,
    targets,
    metadata,
    tags,
  };

  return {
    projects: {
      [projectRoot]: project,
    },
  };
}

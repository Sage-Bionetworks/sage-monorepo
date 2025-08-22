import {
  CreateNodesContext,
  createNodesFromFiles,
  CreateNodesV2,
  detectPackageManager,
  ProjectConfiguration,
  readJsonFile,
  workspaceRoot,
  writeJsonFile,
} from '@nx/devkit';
import { calculateHashForCreateNodes } from '@nx/devkit/src/utils/calculate-hash-for-create-nodes';
import { getLockFileName } from '@nx/js';
import { existsSync } from 'fs';
import { hashObject } from 'nx/src/hasher/file-hasher';
import { workspaceDataDirectory } from 'nx/src/utils/cache-directory';
import { dirname, join } from 'path';
import { buildProjectConfiguration } from './build-project-configuration';
import { createPluginConfiguration, SageMonorepoPluginOptions } from './plugin-configuration';
import { SageMonorepoProjectConfiguration } from './project-configuration';
import { ProjectConfigurationBuilderOptions } from './project-configuration-builder-options';
import { inferProjectMetadata } from './project-metadata';

function readProjectConfigurationsCache(
  cachePath: string,
): Record<string, SageMonorepoProjectConfiguration> {
  // console.log(`cachePath: ${cachePath}`);
  return existsSync(cachePath) ? readJsonFile(cachePath) : {};
}

function writeProjectConfigurationsToCache(
  cachePath: string,
  results: Record<string, SageMonorepoProjectConfiguration>,
) {
  writeJsonFile(cachePath, results);
}

const projectFilePattern =
  '{apps,libs}/{openchallenges,agora,sage,sandbox,iatlas,amp-als,model-ad,explorers,observability,bixarena}/**/project.json';

export const createNodesV2: CreateNodesV2<SageMonorepoPluginOptions> = [
  projectFilePattern,
  async (configFilePaths, options, context) => {
    options ??= {};
    const optionsHash = hashObject(options);

    // Reads the cached targets for all the projects
    const cachePath = join(workspaceDataDirectory, `sage-monorepo-${optionsHash}.hash`);
    const projectConfigurationsCache = readProjectConfigurationsCache(cachePath);
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
  options: SageMonorepoPluginOptions | undefined,
  context: CreateNodesContext,
  projectConfigurationsCache: Record<string, SageMonorepoProjectConfiguration>,
) {
  const projectRoot = dirname(configFilePath);

  // Content of the project file
  const projectFileContent: ProjectConfiguration = readJsonFile(configFilePath);

  const projectName = projectFileContent.name;
  if (typeof projectName !== 'string') {
    throw new Error('Project name is undefined or not a valid string.');
  }
  // console.log(`projectName: ${projectName}`);

  const projectMetadata = inferProjectMetadata(workspaceRoot, projectRoot, projectFileContent);
  // console.log(`projectMetadata: ${JSON.stringify(projectMetadata)}`);

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
    projectMetadata,
    projectConfiguration: projectFileContent,
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

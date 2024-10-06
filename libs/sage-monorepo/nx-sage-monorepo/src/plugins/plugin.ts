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
import { createPluginConfiguration } from './sage-monorepo-plugin-configuration';
import { SageMonorepoPluginOptions } from './sage-monorepo-plugin-options';
import { buildProjectConfiguration } from './build-project-configuration';
import { inferProjectType } from './project-type';
import { inferProjectBuilder } from './project-builder';

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

const projectFilePattern = '{apps,libs}/**/project.json';

export const createNodesV2: CreateNodesV2<SageMonorepoPluginOptions> = [
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
  options: SageMonorepoPluginOptions | undefined,
  context: CreateNodesContext,
  projectConfigurationsCache: Record<string, SageMonorepoProjectConfiguration>,
) {
  const projectRoot = dirname(configFilePath);
  // Do not create a project if project.json and Dockerfile isn't there.
  const siblingFiles = readdirSync(join(context.workspaceRoot, projectRoot));
  if (!siblingFiles.includes('project.json') && !siblingFiles.includes('Dockerfile')) {
    return {};
  }

  // Content of the project file
  const projectFileContent: ProjectConfiguration = readJsonFile(configFilePath);

  const projectName = projectFileContent.name;
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

  projectConfigurationsCache[hash] ??= await buildProjectConfiguration(projectRoot, pluginConfig);

  const { targets, metadata } = projectConfigurationsCache[hash];
  const project: ProjectConfiguration = {
    root: projectRoot,
    targets,
    metadata,
  };

  return {
    projects: {
      [projectRoot]: project,
    },
  };
}

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
import { buildProjectTargets } from './build-project-targets';

function readTargetsCache(cachePath: string): Record<string, SageMonorepoProjectConfiguration> {
  console.log(`cachePath: ${cachePath}`);
  return existsSync(cachePath) ? readJsonFile(cachePath) : {};
}

function writeTargetsToCache(
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
    const targetsCache = readTargetsCache(cachePath);
    try {
      return await createNodesFromFiles(
        (configFile, options, context) => {
          return createNodesInternal(configFile, options, context, targetsCache);
        },
        configFilePaths,
        options,
        context,
      );
    } finally {
      writeTargetsToCache(cachePath, targetsCache);
    }
  },
];

async function createNodesInternal(
  configFilePath: string,
  options: SageMonorepoPluginOptions | undefined,
  context: CreateNodesContext,
  targetsCache: Record<string, SageMonorepoProjectConfiguration>,
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

  const config = createPluginConfiguration(options || {});

  // We do not want to alter how the hash is calculated, so appending the config file path to the
  // hash to prevent the project files overwriting the target cache created by the other project
  const hash =
    (await calculateHashForCreateNodes(projectRoot, config, context, [
      getLockFileName(detectPackageManager(context.workspaceRoot)),
    ])) + configFilePath;

  // TODO: build the targets only if they don't exist in the cache
  const projectTargets = await buildProjectTargets(projectRoot, config);
  targetsCache[hash] ??= projectTargets;

  const { targets, metadata } = targetsCache[hash];
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

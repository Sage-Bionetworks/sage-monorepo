import {
  CreateNodesContext,
  createNodesFromFiles,
  CreateNodesV2,
  detectPackageManager,
  ProjectConfiguration,
  readJsonFile,
  TargetConfiguration,
  writeJsonFile,
} from '@nx/devkit';
import { hashObject } from 'nx/src/hasher/file-hasher';
import { workspaceDataDirectory } from 'nx/src/utils/cache-directory';
import { calculateHashForCreateNodes } from '@nx/devkit/src/utils/calculate-hash-for-create-nodes';
import { dirname, join } from 'path';
import { existsSync, readdirSync } from 'fs';
import { getLockFileName } from '@nx/js';
import { buildImageTarget } from './build-image-targets';

export interface SageMonorepoPluginOptions {
  buildImageTargetName?: string;
}

export interface SageMonorepoPluginConfig {
  buildImageTargetName: string;
}

type SageMonorepoProjectTargets = Pick<ProjectConfiguration, 'targets' | 'metadata'>;

function readTargetsCache(cachePath: string): Record<string, SageMonorepoProjectTargets> {
  console.log(`cachePath: ${cachePath}`);
  return existsSync(cachePath) ? readJsonFile(cachePath) : {};
}

function writeTargetsToCache(
  cachePath: string,
  results: Record<string, SageMonorepoProjectTargets>,
) {
  writeJsonFile(cachePath, results);
}

const projectFilePattern = '{apps,libs}/**/project.json';

export const createNodesV2: CreateNodesV2<SageMonorepoPluginOptions> = [
  projectFilePattern,
  async (configFilePaths, options, context) => {
    console.log('Welcome to createNodesV2');
    console.log(`configFilePaths: ${configFilePaths}`);
    console.log(`options: ${JSON.stringify(options)}`);

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
  targetsCache: Record<string, SageMonorepoProjectTargets>,
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

  const config = createConfig(options || {});

  // We do not want to alter how the hash is calculated, so appending the config file path to the
  // hash to prevent the project files overwriting the target cache created by the other project
  const hash =
    (await calculateHashForCreateNodes(projectRoot, config, context, [
      getLockFileName(detectPackageManager(context.workspaceRoot)),
    ])) + configFilePath;

  const dockerizedAppTargets = await buildDockerizedAppTargets(projectRoot, config);
  targetsCache[hash] ??= dockerizedAppTargets;

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

async function buildDockerizedAppTargets(
  projectRoot: string,
  config: SageMonorepoPluginConfig,
): Promise<SageMonorepoProjectTargets> {
  const targets: Record<string, TargetConfiguration> = {};

  targets[config.buildImageTargetName] = await buildImageTarget(projectRoot);

  const metadata = {};
  return { targets, metadata };
}

function createConfig({
  buildImageTargetName = 'build-image',
}: SageMonorepoPluginOptions): SageMonorepoPluginConfig {
  return {
    buildImageTargetName,
  };
}

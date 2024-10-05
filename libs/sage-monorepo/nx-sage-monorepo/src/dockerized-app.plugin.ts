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

export interface DockerizedAppPluginOptions {
  buildImageTargetName?: string;
}

export interface DockerizedAppPluginConfig {
  buildImageTargetName: string;
}

type DockerizedAppTargets = Pick<ProjectConfiguration, 'targets' | 'metadata'>;

function readTargetsCache(cachePath: string): Record<string, DockerizedAppTargets> {
  return existsSync(cachePath) ? readJsonFile(cachePath) : {};
}

function writeTargetsToCache(cachePath: string, results: Record<string, DockerizedAppTargets>) {
  writeJsonFile(cachePath, results);
}

const dockerfileGlob = 'apps/openchallenges/apex/Dockerfile';

export const createNodesV2: CreateNodesV2<DockerizedAppPluginOptions> = [
  dockerfileGlob,
  async (configFilePaths, options, context) => {
    console.log('Welcome to createNodesV2');
    console.log(`configFilePaths: ${configFilePaths}`);
    const optionsHash = hashObject(options || {});
    const cachePath = join(workspaceDataDirectory, `sage-monorepo-${optionsHash}.hash`);
    const targetsCache = readTargetsCache(cachePath);
    try {
      return await createNodesFromFiles(
        (configFile, options, context) => {
          const config = normalizeOptions(options || {});
          return createNodesInternal(configFile, config, context, targetsCache);
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
  config: DockerizedAppPluginConfig,
  context: CreateNodesContext,
  targetsCache: Record<string, DockerizedAppTargets>,
) {
  const projectRoot = dirname(configFilePath);
  // Do not create a project if project.json and Dockerfile isn't there.
  const siblingFiles = readdirSync(join(context.workspaceRoot, projectRoot));
  if (!siblingFiles.includes('project.json') && !siblingFiles.includes('Dockerfile')) {
    return {};
  }

  // We do not want to alter how the hash is calculated, so appending the config file path to the hash
  // to prevent the project files overwriting the target cache created by the other
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
  config: DockerizedAppPluginConfig,
): Promise<DockerizedAppTargets> {
  const targets: Record<string, TargetConfiguration> = {};

  targets[config.buildImageTargetName] = await buildImageTarget(projectRoot);

  const metadata = {};
  return { targets, metadata };
}

async function buildImageTarget(projectRoot: string) {
  return {
    executor: '@nx-tools/nx-container:build',
    outputs: [],

    options: {
      context: projectRoot,
    },
    cache: false,
    configurations: {
      local: {
        metadata: {
          images: ['ghcr.io/sage-bionetworks/{projectName}'],
          tags: ['type=edge,branch=main', 'type=raw,value=local', 'type=sha'],
        },
      },
      ci: {
        metadata: {
          images: ['ghcr.io/sage-bionetworks/{projectName}'],
          tags: ['type=semver,pattern={{version}},value=${VERSION}', 'type=sha'],
        },
        push: true,
      },
    },
    defaultConfiguration: 'local',
  };
}

function normalizeOptions(options: DockerizedAppPluginOptions): DockerizedAppPluginConfig {
  return {
    buildImageTargetName: options.buildImageTargetName ?? 'build-image',
  };
}

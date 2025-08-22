import { ProjectConfiguration, ProjectType } from '@nx/devkit';
import { join } from 'path';
import { readdirSync, readFileSync } from 'fs';

export type Builder = 'esbuild' | 'webpack' | 'gradle' | 'maven' | 'uv';
// export type Linter = 'eslint' | 'pylint';
// export type TypeChecker = 'mypy' | 'pyright';
// export type TestingTool = 'pytest' | null;
// export type Formatter = 'Black' | 'Prettier';
export type ContainerType = 'docker' | 'singularity';
// export type Language = 'python' | 'typescript' | 'javascript';
export type Framework = 'angular';
export type BaseImageType = 'postgres' | 'apex' | 'custom';

export type ProjectMetadata = {
  projectType: ProjectType;
  builder: Builder | null;
  // linter: Linter;
  // typeChecker: TypeChecker[];
  // testing: {
  //   unit: TestingTool;
  //   integration: TestingTool;
  //   e2e: TestingTool;
  // };
  // formatter: Formatter;
  containerType: ContainerType | null;
  // language: Language;
  framework: Framework | null;
  baseImageType: BaseImageType | null;
};

export function inferProjectMetadata(
  workspaceRoot: string,
  projectRoot: string,
  localProjectConfiguration: ProjectConfiguration,
): ProjectMetadata {
  const siblingFiles = readdirSync(join(workspaceRoot, projectRoot));
  return {
    projectType: inferProjectType(projectRoot),
    builder: inferBuilder(siblingFiles, localProjectConfiguration),
    containerType: inferContainerType(siblingFiles),
    framework: inferFramework(localProjectConfiguration),
    baseImageType: inferBaseImageType(projectRoot, siblingFiles),
  };
}

function inferProjectType(projectRoot: string): ProjectType {
  if (projectRoot.startsWith('apps/')) {
    return 'application';
  } else if (projectRoot.startsWith('libs/')) {
    return 'library';
  }

  throw new Error(`Unknown project type for project root: ${projectRoot}`);
}

function inferBuilder(
  siblingFiles: string[],
  localProjectConfiguration: ProjectConfiguration,
): Builder | null {
  if (siblingFiles.includes('uv.lock')) return 'uv';
  if (['build.gradle', 'build.gradle.kts'].some((file) => siblingFiles.includes(file))) {
    return 'gradle';
  }

  const executor = localProjectConfiguration?.targets?.['build']?.executor ?? '';
  const webpackExecutors = [
    '@angular-devkit/build-angular:browser',
    '@nx/angular:application',
    '@nx/webpack:webpack',
  ];

  if (webpackExecutors.includes(executor)) {
    return 'webpack';
  }

  return null;
}

function inferContainerType(siblingFiles: string[]): ContainerType | null {
  if (siblingFiles.includes('Dockerfile')) return 'docker';

  return null;
}

function inferFramework(localProjectConfiguration: ProjectConfiguration): Framework | null {
  const buildExecutor = localProjectConfiguration?.targets?.['build']?.executor ?? '';
  const angularBuildExecutors = ['@angular-devkit/build-angular:browser'];

  if (angularBuildExecutors.includes(buildExecutor)) {
    return 'angular';
  }

  return null;
}

function inferBaseImageType(projectRoot: string, siblingFiles: string[]): BaseImageType | null {
  // Only process projects with Dockerfiles
  if (!siblingFiles.includes('Dockerfile')) return null;

  // Check for specific project types based on project path for postgres and apex
  // Only target openchallenges and amp-als projects
  if (projectRoot.includes('openchallenges') || projectRoot.includes('amp-als')) {
    if (projectRoot.includes('postgres')) return 'postgres';
    if (projectRoot.includes('apex')) return 'apex';
  }

  // All other projects with Dockerfiles are treated as custom
  return 'custom';
}

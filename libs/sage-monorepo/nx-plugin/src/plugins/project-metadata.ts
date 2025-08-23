import { ProjectConfiguration, ProjectType } from '@nx/devkit';
import { join } from 'path';
import { readdirSync } from 'fs';

export type Builder = 'esbuild' | 'webpack' | 'gradle' | 'maven' | 'uv';
// export type Linter = 'eslint' | 'pylint';
// export type TypeChecker = 'mypy' | 'pyright';
// export type TestingTool = 'pytest' | null;
// export type Formatter = 'Black' | 'Prettier';
export type ContainerType = 'docker' | 'singularity';
// export type Language = 'python' | 'typescript' | 'javascript';
export type Framework = 'angular';
export type ContainerImageType = 'postgres' | 'caddy' | 'custom';

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
  containerImageType: ContainerImageType | null;
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
    containerType: inferContainerType(localProjectConfiguration, siblingFiles),
    framework: inferFramework(localProjectConfiguration),
    containerImageType: inferContainerImageType(localProjectConfiguration, siblingFiles),
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

function inferContainerType(
  localProjectConfiguration: ProjectConfiguration,
  siblingFiles: string[],
): ContainerType | null {
  const tags = localProjectConfiguration.tags || [];

  // Check for new container-image tags (opt-in to centralized system)
  if (tags.some((tag) => tag.startsWith('container-image:'))) {
    return 'docker';
  }

  // Fallback: existing behavior for projects not using new system
  if (siblingFiles.includes('Dockerfile')) {
    return 'docker';
  }

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

function inferContainerImageType(
  localProjectConfiguration: ProjectConfiguration,
  siblingFiles: string[],
): ContainerImageType | null {
  const tags = localProjectConfiguration.tags || [];

  // Extract base image type from container-image tags
  const containerImageTag = tags.find((tag) => tag.startsWith('container-image:'));

  if (containerImageTag) {
    const imageType = containerImageTag.split(':')[1];

    // No validation here - let task execution handle invalid values
    switch (imageType) {
      case 'postgres':
        return 'postgres';
      case 'caddy':
        return 'caddy';
      default:
        // For any other value (including 'custom' and invalid values), return 'custom'
        // Task generation will validate and provide helpful errors for invalid values
        return 'custom';
    }
  }

  // Fallback: check for physical Dockerfile (for existing projects)
  if (siblingFiles.includes('Dockerfile')) {
    return 'custom';
  }

  // No container-image tag and no Dockerfile means no container capabilities
  return null;
}

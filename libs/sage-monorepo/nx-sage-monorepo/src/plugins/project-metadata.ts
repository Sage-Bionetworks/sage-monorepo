import { ProjectConfiguration, ProjectType } from '@nx/devkit';
import { join } from 'path';
import { readdirSync } from 'fs';

export type Builder = 'esbuild' | 'webpack' | 'gradle' | 'maven' | 'poetry' | null;
// export type Linter = 'eslint' | 'pylint';
// export type TypeChecker = 'mypy' | 'pyright';
// export type TestingTool = 'pytest' | null;
// export type Formatter = 'Black' | 'Prettier';
// export type ContainerType = 'Docker' | 'Singularity';
// export type Language = 'python' | 'typescript' | 'javascript';
// export type Framework = 'Flask' | 'React' | 'Angular' | 'Vue' | null;

export type ProjectMetadata = {
  projectType: ProjectType;
  builder: Builder;
  // linter: Linter;
  // typeChecker: TypeChecker[];
  // testing: {
  //   unit: TestingTool;
  //   integration: TestingTool;
  //   e2e: TestingTool;
  // };
  // formatter: Formatter;
  // containerType: ContainerType;
  // language: Language;
  // framework: Framework;
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
  };
}

export function inferProjectType(projectRoot: string): ProjectType {
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
): Builder {
  if (siblingFiles.includes('poetry.lock')) return 'poetry';
  if (siblingFiles.includes('build.gradle')) return 'gradle';
  if (
    localProjectConfiguration?.targets?.['build']?.executor ===
    '@angular-devkit/build-angular:browser'
  ) {
    return 'webpack';
  }
  return null;
}

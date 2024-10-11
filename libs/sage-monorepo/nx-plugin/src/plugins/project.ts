import { ProjectType } from '@nx/devkit';

export type Builder = 'esbuild' | 'gradle' | 'poetry' | 'wheel';
export type Linter = 'eslint' | 'pylint';
export type TypeChecker = 'mypy' | 'pyright';
export type TestingTool = 'pytest' | null;
export type Formatter = 'Black' | 'Prettier';
export type ContainerType = 'Docker' | 'Singularity';
export type Language = 'python' | 'typescript' | 'javascript';
export type Framework = 'Flask' | 'React' | 'Angular' | 'Vue' | null;

export type ProjectMetadata = {
  projectType: ProjectType;
  builder: Builder;
  linter: Linter;
  typeChecker: TypeChecker[];
  testing: {
    unit: TestingTool;
    integration: TestingTool;
    e2e: TestingTool;
  };
  formatter: Formatter;
  containerType: ContainerType;
  language: Language;
  framework: Framework;
};

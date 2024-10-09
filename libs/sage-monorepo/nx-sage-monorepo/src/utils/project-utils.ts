import { normalizePath } from 'nx/src/utils/path';

export type ProjectType = 'APP';

const projectTypeRegExps: Record<ProjectType, RegExp> = {
  APP: /^apps\/(.+)apex\/project.json$/,
};

export const getProjectType = (projectPath: string): ProjectType | undefined => {
  // console.log(`projectPath: ${projectPath}`);
  return Object.entries(projectTypeRegExps).find(([, regExp]) =>
    regExp.test(normalizePath(projectPath)),
  )?.[0] as ProjectType;
};

export const getProjectRoot = (projectPath: string): string => {
  const normalizedPath = normalizePath(projectPath);
  return normalizedPath.replace('/project.json', '');
};

export const getProjectName = (projectPath: string): string | undefined =>
  getProjectRoot(projectPath).split('/').pop();

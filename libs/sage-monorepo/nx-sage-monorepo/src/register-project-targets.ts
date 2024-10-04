import { TargetConfiguration } from 'nx/src/config/workspace-json-project-json';
import { getProjectType, ProjectType } from './utils/project-utils';
import { registerAppTargets } from './register-app-targets';

export const projectFilePatterns = ['project.json'];

const projectTypeToRegisterRouting: Record<
  ProjectType,
  (projectPath: string) => Record<string, TargetConfiguration>
> = {
  APP: registerAppTargets,
};

export function registerProjectTargets(projectPath: string): Record<string, TargetConfiguration> {
  const projectType = getProjectType(projectPath);
  // console.log(`project type: ${projectType}`);
  const registerProjectTargetFn = projectType
    ? projectTypeToRegisterRouting[projectType]
    : undefined;

  return registerProjectTargetFn ? registerProjectTargetFn(projectPath) : {};
}

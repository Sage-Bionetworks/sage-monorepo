// export type ProjectType = 'APP' | 'LIB';

import { ProjectType } from '@nx/devkit';

export type ProgrammingLanguage = 'TypeScript' | 'Java';

export function inferProjectType(projectRoot: string): ProjectType {
  if (projectRoot.startsWith('apps/')) {
    return 'application';
  } else if (projectRoot.startsWith('libs/')) {
    return 'library';
  }

  throw new Error(`Unknown project type for project root: ${projectRoot}`);
}

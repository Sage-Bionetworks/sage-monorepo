import { ProjectConfiguration } from '@nx/devkit';

export type CreateProjectConfiguration = (
  projectRoot: string,
) => Omit<ProjectConfiguration, 'root'>;

export type SageMonorepoProjectConfiguration = Pick<ProjectConfiguration, 'targets' | 'metadata'>;

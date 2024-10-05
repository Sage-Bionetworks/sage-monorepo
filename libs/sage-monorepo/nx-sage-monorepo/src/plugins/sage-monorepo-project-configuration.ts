import { ProjectConfiguration } from '@nx/devkit';

export type SageMonorepoProjectConfiguration = Pick<ProjectConfiguration, 'targets' | 'metadata'>;

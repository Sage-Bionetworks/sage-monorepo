import { z } from 'zod';

/**
 * Standard environment values
 * These are the canonical environment names that match config file names
 */
export const ENVIRONMENTS = ['dev', 'test', 'stage', 'prod'] as const;

/**
 * Extended environment values including common aliases
 * Allows flexibility in configuration files while normalizing to canonical values
 */
export const ENVIRONMENT_VALUES = [
  ...ENVIRONMENTS,
  'development', // alias for 'dev'
  'staging', // alias for 'stage'
  'production', // alias for 'prod'
] as const;

/**
 * Type for environment values
 */
export type Environment = (typeof ENVIRONMENTS)[number];
export type EnvironmentValue = (typeof ENVIRONMENT_VALUES)[number];

/**
 * Base configuration schema that all app configs should extend
 * Provides standard 'environment' property
 */
export const BaseConfigSchema = z.object({
  environment: z.enum(ENVIRONMENT_VALUES, {
    message: `Environment must be one of: ${ENVIRONMENT_VALUES.join(', ')}`,
  }),
});

/**
 * Base configuration type
 */
export type BaseConfig = z.infer<typeof BaseConfigSchema>;

/**
 * Normalize environment value to canonical form
 * Maps aliases to their canonical values
 */
export function normalizeEnvironment(env: EnvironmentValue): Environment {
  const mapping: Record<string, Environment> = {
    development: 'dev',
    staging: 'stage',
    production: 'prod',
    dev: 'dev',
    test: 'test',
    stage: 'stage',
    prod: 'prod',
  };
  return mapping[env] || 'dev';
}

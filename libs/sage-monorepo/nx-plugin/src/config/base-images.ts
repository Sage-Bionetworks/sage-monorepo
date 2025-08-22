export interface BaseImageConfig {
  name: string;
  version: string;
  registry?: string;
}

export interface BaseImagesRegistry {
  postgres: BaseImageConfig;
  caddy: BaseImageConfig;
  // Add other base images as needed in the future
}

export const BASE_IMAGES: BaseImagesRegistry = {
  postgres: {
    name: 'postgres',
    version: '16.9-bullseye',
    registry: 'mirror.gcr.io',
  },
  caddy: {
    name: 'caddy',
    version: '2.9.1', // Using the newer version from amp-als
    registry: 'mirror.gcr.io',
  },
};

/**
 * Get the full base image string (registry/name:version) for a given image type
 */
export function getBaseImageString(imageKey: keyof BaseImagesRegistry): string {
  const config = BASE_IMAGES[imageKey];
  const registry = config.registry ? `${config.registry}/` : '';
  return `${registry}${config.name}:${config.version}`;
}

/**
 * Get base image configuration for a given image type
 */
export function getBaseImageConfig(imageKey: keyof BaseImagesRegistry): BaseImageConfig {
  return BASE_IMAGES[imageKey];
}

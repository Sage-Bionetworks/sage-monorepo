export interface ContainerImageConfig {
  name: string;
  version: string;
  registry?: string;
}

export interface ContainerImagesRegistry {
  postgres: ContainerImageConfig;
  caddy: ContainerImageConfig;
  // Add other container images as needed in the future
}

export const CONTAINER_IMAGES: ContainerImagesRegistry = {
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
 * Get the full container image string (registry/name:version) for a given image type
 */
export function getContainerImageString(imageKey: keyof ContainerImagesRegistry): string {
  const config = CONTAINER_IMAGES[imageKey];
  const registry = config.registry ? `${config.registry}/` : '';
  return `${registry}${config.name}:${config.version}`;
}

/**
 * Get container image configuration for a given image type
 */
export function getContainerImageConfig(
  imageKey: keyof ContainerImagesRegistry,
): ContainerImageConfig {
  return CONTAINER_IMAGES[imageKey];
}

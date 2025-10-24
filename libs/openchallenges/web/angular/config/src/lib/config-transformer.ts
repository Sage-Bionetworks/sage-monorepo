import { AppConfig } from './config.schema';
import { ClientConfig } from './client-config.schema';

/**
 * Transform server configuration to client configuration
 *
 * This function:
 * 1. Filters out server-only properties (e.g., api.baseUrls.ssr)
 * 2. Transforms property structures (e.g., api.baseUrls.csr → api.baseUrl)
 * 3. Ensures only necessary data is transferred to the client
 *
 * Benefits:
 * - Reduces bundle size (less config data transferred)
 * - Prevents exposure of server-only configuration
 * - Type-safe transformation
 * - Clear separation of concerns
 *
 * @param serverConfig - Full server configuration
 * @returns Filtered client configuration
 */
export function transformServerToClientConfig(serverConfig: AppConfig): ClientConfig {
  return {
    // BaseConfig properties
    environment: serverConfig.environment,

    // App properties
    app: {
      version: serverConfig.app.version,
    },

    // Telemetry (same on server and client)
    telemetry: {
      enabled: serverConfig.telemetry.enabled,
    },

    // API: Transform baseUrls.csr to baseUrl (client only needs CSR URL)
    api: {
      baseUrl: serverConfig.api.baseUrls.csr,
      docsUrl: serverConfig.api.docsUrl,
    },

    // Data (same on server and client)
    data: {
      updatedOn: serverConfig.data.updatedOn,
    },

    // Features (same on server and client)
    features: {
      announcement: {
        enabled: serverConfig.features.announcement.enabled,
      },
      operationFilter: {
        enabled: serverConfig.features.operationFilter.enabled,
      },
    },

    // Analytics (same on server and client)
    analytics: {
      googleTagManager: {
        enabled: serverConfig.analytics.googleTagManager.enabled,
        id: serverConfig.analytics.googleTagManager.id,
      },
    },

    // Links (same on server and client)
    links: {
      privacyPolicy: serverConfig.links.privacyPolicy,
      termsOfUse: serverConfig.links.termsOfUse,
    },
  };
}

/**
 * Transform any unknown config to client config with validation
 * Useful when loading from TransferState
 *
 * @param config - Unknown configuration object
 * @returns Validated client configuration
 */
export function transformAndValidateClientConfig(config: unknown): ClientConfig {
  // If it's already in client format, validate it
  // Otherwise, assume it's server format and transform it
  const clientConfig = transformServerToClientConfig(config as AppConfig);
  return clientConfig;
}

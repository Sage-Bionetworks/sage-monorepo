import { EnvironmentProviders, InjectionToken, makeEnvironmentProviders } from '@angular/core';
import { VisualizationOverviewPane } from '@sagebionetworks/explorers/models';

/**
 * App-level configuration for explorer applications.
 */
export interface ExplorersAppConfig {
  visualizationOverviewPanes: VisualizationOverviewPane[];
}

export const EXPLORERS_APP_CONFIG = new InjectionToken<ExplorersAppConfig>('EXPLORERS_APP_CONFIG');

export function provideExplorersConfig(config: ExplorersAppConfig): EnvironmentProviders {
  return makeEnvironmentProviders([{ provide: EXPLORERS_APP_CONFIG, useValue: config }]);
}

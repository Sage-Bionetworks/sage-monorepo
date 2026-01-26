import { EnvironmentProviders, InjectionToken, makeEnvironmentProviders } from '@angular/core';
import { VisualizationOverviewPane } from '@sagebionetworks/explorers/models';

/**
 * App-level configuration for explorer applications.
 */
export interface ExplorersConfig {
  visualizationOverviewPanes: VisualizationOverviewPane[];
}

export const EXPLORERS_CONFIG = new InjectionToken<ExplorersConfig>('EXPLORERS_CONFIG');

export function provideExplorersConfig(config: ExplorersConfig): EnvironmentProviders {
  return makeEnvironmentProviders([{ provide: EXPLORERS_CONFIG, useValue: config }]);
}

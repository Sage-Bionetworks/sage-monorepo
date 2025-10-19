import {
  provideComparisonToolFilterService,
  provideComparisonToolService,
} from '@sagebionetworks/explorers/services';
import {
  mockComparisonToolConfigs,
  provideLoadingIconColors,
} from '@sagebionetworks/explorers/testing';
import { LoadingContainerComponent } from '@sagebionetworks/explorers/util';
import { render } from '@testing-library/angular';
import { BaseComparisonToolComponent } from './base-comparison-tool.component';

async function setup() {
  const { fixture } = await render(BaseComparisonToolComponent, {
    imports: [LoadingContainerComponent],
    providers: [
      provideLoadingIconColors(),
      ...provideComparisonToolService({
        configs: mockComparisonToolConfigs,
      }),
      ...provideComparisonToolFilterService(),
    ],
  });

  const component = fixture.componentInstance;
  return { component, fixture };
}

describe('Base Comparison Tool Component', () => {
  it('should create', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });

  it('should have default values', async () => {
    const { component } = await setup();
    expect(component.isLoading()).toBe(true);
    expect(component.resultsCount()).toBe(0);
  });
});

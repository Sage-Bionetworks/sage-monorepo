import {
  provideComparisonToolFilterService,
  provideComparisonToolService,
  SvgIconService,
} from '@sagebionetworks/explorers/services';
import {
  mockComparisonToolConfigs,
  provideLoadingIconColors,
  SvgIconServiceStub,
} from '@sagebionetworks/explorers/testing';
import { LoadingContainerComponent } from '@sagebionetworks/explorers/util';
import { render } from '@testing-library/angular';
import { ComparisonToolComponent } from './comparison-tool.component';

async function setup() {
  const { fixture } = await render(ComparisonToolComponent, {
    imports: [LoadingContainerComponent],
    providers: [
      provideLoadingIconColors(),
      ...provideComparisonToolService({
        configs: mockComparisonToolConfigs,
      }),
      ...provideComparisonToolFilterService(),
      { provide: SvgIconService, useClass: SvgIconServiceStub },
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

  it('should have loading results count while loading', async () => {
    const { component } = await setup();
    expect(component.isLoading()).toBe(true);
    expect(component.loadingResultsCount()).toBe(mockComparisonToolConfigs[0].row_count);
  });
});

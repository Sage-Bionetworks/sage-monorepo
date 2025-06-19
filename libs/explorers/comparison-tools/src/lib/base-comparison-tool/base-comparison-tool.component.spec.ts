import { render } from '@testing-library/angular';
import { BaseComparisonToolComponent } from './base-comparison-tool.component';
import { LoadingContainerComponent } from '@sagebionetworks/explorers/util';
import { getModelAdTestProviders } from '@sagebionetworks/model-ad/testing';

async function setup() {
  const { fixture } = await render(BaseComparisonToolComponent, {
    imports: [LoadingContainerComponent],
    providers: [getModelAdTestProviders()],
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

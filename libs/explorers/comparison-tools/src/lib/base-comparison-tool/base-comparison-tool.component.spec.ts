import { render } from '@testing-library/angular';
import { BaseComparisonToolComponent } from './base-comparison-tool.component';
import { LoadingContainerComponent } from '@sagebionetworks/explorers/util';

async function setup(componentProperties = {}) {
  const { fixture } = await render(BaseComparisonToolComponent, {
    componentProperties: {
      isLoading: true,
      loadingIconColors: { colorInnermost: '#000', colorCentral: '#fff', colorOutermost: '#ccc' },
      resultsCount: 0,
      ...componentProperties,
    },
    imports: [BaseComparisonToolComponent, LoadingContainerComponent],
  });

  const component = fixture.componentInstance;
  return { component };
}

describe('Base Comparison Tool Component', () => {
  it('should create', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });
});

import { render } from '@testing-library/angular';
import { BaseComparisonToolComponent } from './base-comparison-tool.component';
import { LoadingContainerComponent } from '@sagebionetworks/explorers/util';
import { LOADING_ICON_COLORS } from '@sagebionetworks/explorers/models';
import { MODEL_AD_LOADING_ICON_COLORS } from '@sagebionetworks/model-ad/config';

async function setup() {
  const { fixture } = await render(BaseComparisonToolComponent, {
    imports: [LoadingContainerComponent],
    providers: [
      {
        provide: LOADING_ICON_COLORS,
        useValue: MODEL_AD_LOADING_ICON_COLORS,
      },
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

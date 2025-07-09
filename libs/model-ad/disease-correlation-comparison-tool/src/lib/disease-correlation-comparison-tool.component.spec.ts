import { BaseComparisonToolComponent } from '@sagebionetworks/explorers/comparison-tools';
import { provideLoadingIconColors } from '@sagebionetworks/explorers/testing';
import { MODEL_AD_LOADING_ICON_COLORS } from '@sagebionetworks/model-ad/config';
import { render } from '@testing-library/angular';
import { MessageService } from 'primeng/api';
import { DiseaseCorrelationComparisonToolComponent } from './disease-correlation-comparison-tool.component';

async function setup() {
  const { fixture } = await render(DiseaseCorrelationComparisonToolComponent, {
    imports: [BaseComparisonToolComponent],
    providers: [MessageService, provideLoadingIconColors(MODEL_AD_LOADING_ICON_COLORS)],
  });

  const component = fixture.componentInstance;
  return { component };
}

describe('DiseaseCorrelationComparisonToolComponent', () => {
  it('should create', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });
});

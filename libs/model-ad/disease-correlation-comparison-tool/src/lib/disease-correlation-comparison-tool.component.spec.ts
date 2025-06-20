import { render } from '@testing-library/angular';
import { DiseaseCorrelationComparisonToolComponent } from './disease-correlation-comparison-tool.component';
import { BaseComparisonToolComponent } from '@sagebionetworks/explorers/comparison-tools';
import { MessageService } from 'primeng/api';
import { getModelAdTestProviders } from '@sagebionetworks/model-ad/testing';

async function setup() {
  const { fixture } = await render(DiseaseCorrelationComparisonToolComponent, {
    imports: [BaseComparisonToolComponent],
    providers: [MessageService, ...getModelAdTestProviders()],
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

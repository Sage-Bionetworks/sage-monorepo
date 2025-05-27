import { render } from '@testing-library/angular';
import { DiseaseCorrelationComparisonToolComponent } from './disease-correlation-comparison-tool.component';
import { BaseComparisonToolComponent } from '@sagebionetworks/explorers/comparison-tools';

async function setup() {
  const { fixture } = await render(DiseaseCorrelationComparisonToolComponent, {
    imports: [BaseComparisonToolComponent],
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

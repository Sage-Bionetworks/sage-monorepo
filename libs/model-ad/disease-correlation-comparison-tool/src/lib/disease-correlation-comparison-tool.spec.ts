import { render, screen } from '@testing-library/angular';
import { DiseaseCorrelationComparisonToolComponent } from './disease-correlation-comparison-tool.component';
import { BaseComparisonToolComponent } from '@sagebionetworks/explorers/comparison-tools';

async function setup() {
  await render(DiseaseCorrelationComparisonToolComponent, {
    imports: [BaseComparisonToolComponent],
  });
}

describe('DiseaseCorrelationComparisonToolComponent', () => {
  it('should display heading', async () => {
    await setup();
    const heading = screen.getByRole('heading', { level: 1 });
    expect(heading).toHaveTextContent('Disease Correlation');
  });
});

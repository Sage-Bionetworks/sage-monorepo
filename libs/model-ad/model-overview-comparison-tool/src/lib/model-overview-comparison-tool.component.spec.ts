import { render, screen } from '@testing-library/angular';
import { ModelOverviewComparisonToolComponent } from './model-overview-comparison-tool.component';
import { BaseComparisonToolComponent } from '@sagebionetworks/explorers/comparison-tools';

async function setup() {
  await render(ModelOverviewComparisonToolComponent, {
    imports: [BaseComparisonToolComponent],
  });
}

describe('ModelOverviewComparisonToolComponent', () => {
  it('should display heading', async () => {
    await setup();
    const heading = screen.getByRole('heading', { level: 1 });
    expect(heading).toHaveTextContent('Model Overview');
  });
});

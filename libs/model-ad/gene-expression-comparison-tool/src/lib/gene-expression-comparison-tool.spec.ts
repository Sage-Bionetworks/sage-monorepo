import { render, screen } from '@testing-library/angular';
import { GeneExpressionComparisonToolComponent } from './gene-expression-comparison-tool.component';
import { BaseComparisonToolComponent } from '@sagebionetworks/explorers/comparison-tools';

async function setup() {
  await render(GeneExpressionComparisonToolComponent, {
    imports: [BaseComparisonToolComponent],
  });
}

describe('GeneExpressionComparisonToolComponent', () => {
  it('should display heading', async () => {
    await setup();
    const heading = screen.getByRole('heading', { level: 1 });
    expect(heading).toHaveTextContent('Gene Expression');
  });
});

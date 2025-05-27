import { render } from '@testing-library/angular';
import { GeneExpressionComparisonToolComponent } from './gene-expression-comparison-tool.component';
import { BaseComparisonToolComponent } from '@sagebionetworks/explorers/comparison-tools';

async function setup() {
  const { fixture } = await render(GeneExpressionComparisonToolComponent, {
    imports: [BaseComparisonToolComponent],
  });

  const component = fixture.componentInstance;
  return { component };
}

describe('GeneExpressionComparisonToolComponent', () => {
  it('should create', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });
});

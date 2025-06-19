import { render } from '@testing-library/angular';
import { GeneExpressionComparisonToolComponent } from './gene-expression-comparison-tool.component';
import { BaseComparisonToolComponent } from '@sagebionetworks/explorers/comparison-tools';
import { MessageService } from 'primeng/api';
import { LOADING_ICON_COLORS } from '@sagebionetworks/explorers/models';
import { MODEL_AD_LOADING_ICON_COLORS } from '@sagebionetworks/model-ad/config';

async function setup() {
  const { fixture } = await render(GeneExpressionComparisonToolComponent, {
    imports: [BaseComparisonToolComponent],
    providers: [
      MessageService,
      {
        provide: LOADING_ICON_COLORS,
        useValue: MODEL_AD_LOADING_ICON_COLORS,
      },
    ],
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

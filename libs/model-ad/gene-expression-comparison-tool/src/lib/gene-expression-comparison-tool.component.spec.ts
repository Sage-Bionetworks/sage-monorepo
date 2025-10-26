import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { BaseComparisonToolComponent } from '@sagebionetworks/explorers/comparison-tools';
import { provideLoadingIconColors } from '@sagebionetworks/explorers/testing';
import { ComparisonToolConfigService } from '@sagebionetworks/model-ad/api-client';
import { MODEL_AD_LOADING_ICON_COLORS } from '@sagebionetworks/model-ad/config';
import { render } from '@testing-library/angular';
import { MessageService } from 'primeng/api';
import { of } from 'rxjs';
import { GeneExpressionComparisonToolComponent } from './gene-expression-comparison-tool.component';
import {
  ComparisonToolService,
  provideComparisonToolFilterService,
} from '@sagebionetworks/explorers/services';

async function setup() {
  const { fixture } = await render(GeneExpressionComparisonToolComponent, {
    imports: [BaseComparisonToolComponent],
    providers: [
      MessageService,
      provideLoadingIconColors(MODEL_AD_LOADING_ICON_COLORS),
      provideHttpClient(),
      provideRouter([]),
      {
        provide: ComparisonToolConfigService,
        useValue: {
          getComparisonToolConfig: jest.fn().mockReturnValue(of([])),
        },
      },
      ComparisonToolService,
      ...provideComparisonToolFilterService(),
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

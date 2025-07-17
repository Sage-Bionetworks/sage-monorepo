import { BaseComparisonToolComponent } from '@sagebionetworks/explorers/comparison-tools';
import { provideLoadingIconColors } from '@sagebionetworks/explorers/testing';
import { MODEL_AD_LOADING_ICON_COLORS } from '@sagebionetworks/model-ad/config';
import { render } from '@testing-library/angular';

import { MessageService } from 'primeng/api';
import { ModelOverviewComparisonToolComponent } from './model-overview-comparison-tool.component';
import { provideHttpClient } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';

class MockComparisonToolService {
  columns = { set: jest.fn(), value: ['Model Type', 'Matched Control', 'Gene Expression'] };
  totalResultsCount = { value: 3 };
  pinnedResultsCount = { value: 0 };

  isLegendVisible = jest.fn().mockReturnValue(false);
  setLegendVisibility = jest.fn();
  toggleLegend = jest.fn();
}

const mockComparisonToolConfig = {
  columns: [{ name: 'Model Type' }, { name: 'Matched Control' }, { name: 'Gene Expression' }],
  // Add other properties if required by the interface
};

async function setup() {
  const { fixture } = await render(ModelOverviewComparisonToolComponent, {
    imports: [BaseComparisonToolComponent],
    providers: [
      MessageService,
      provideLoadingIconColors(MODEL_AD_LOADING_ICON_COLORS),
      provideHttpClient(),
      {
        provide: ActivatedRoute,
        useValue: {
          snapshot: {
            data: {
              comparisonToolConfig: mockComparisonToolConfig,
            },
          },
        },
      },
      {
        provide: ComparisonToolService,
        useClass: MockComparisonToolService,
      },
    ],
  });

  const component = fixture.componentInstance;
  return { component, fixture };
}

describe('ModelOverviewComparisonToolComponent', () => {
  it('should create', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });
});

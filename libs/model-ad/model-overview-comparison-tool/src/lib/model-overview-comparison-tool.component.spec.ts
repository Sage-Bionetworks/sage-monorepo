import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { ComparisonToolComponent } from '@sagebionetworks/explorers/comparison-tool';
import {
  ComparisonToolService,
  PlatformService,
  provideComparisonToolFilterService,
} from '@sagebionetworks/explorers/services';
import { provideLoadingIconColors } from '@sagebionetworks/explorers/testing';
import {
  ComparisonToolConfigService,
  ModelOverviewService,
} from '@sagebionetworks/model-ad/api-client';
import { MODEL_AD_LOADING_ICON_COLORS } from '@sagebionetworks/model-ad/config';
import { render } from '@testing-library/angular';
import { MessageService } from 'primeng/api';
import { of } from 'rxjs';
import { ModelOverviewComparisonToolComponent } from './model-overview-comparison-tool.component';
import { ModelOverviewComparisonToolService } from './services/model-overview-comparison-tool.service';

async function setup() {
  const { fixture } = await render(ModelOverviewComparisonToolComponent, {
    imports: [ComparisonToolComponent],
    providers: [
      MessageService,
      provideLoadingIconColors(MODEL_AD_LOADING_ICON_COLORS),
      provideHttpClient(),
      provideRouter([]),
      {
        provide: PlatformService,
        useValue: { isBrowser: true },
      },
      {
        provide: ComparisonToolConfigService,
        useValue: {
          getComparisonToolConfig: jest.fn().mockReturnValue(of([])),
        },
      },
      {
        provide: ModelOverviewService,
        useValue: {
          getModelOverviews: jest.fn().mockReturnValue(of([])),
        },
      },
      ComparisonToolService,
      ...provideComparisonToolFilterService(),
      ModelOverviewComparisonToolService,
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

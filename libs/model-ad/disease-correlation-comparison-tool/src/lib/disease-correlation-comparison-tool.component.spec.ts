import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { BaseComparisonToolComponent } from '@sagebionetworks/explorers/comparison-tool';
import { ComparisonToolService, PlatformService } from '@sagebionetworks/explorers/services';
import { provideLoadingIconColors } from '@sagebionetworks/explorers/testing';
import {
  ComparisonToolConfigService,
  DiseaseCorrelationService,
} from '@sagebionetworks/model-ad/api-client';
import { MODEL_AD_LOADING_ICON_COLORS } from '@sagebionetworks/model-ad/config';
import { render } from '@testing-library/angular';
import { MessageService } from 'primeng/api';
import { of } from 'rxjs';
import { DiseaseCorrelationComparisonToolComponent } from './disease-correlation-comparison-tool.component';
import { DiseaseCorrelationComparisonToolService } from './services/disease-correlation-comparison-tool.service';

async function setup() {
  const { fixture } = await render(DiseaseCorrelationComparisonToolComponent, {
    imports: [BaseComparisonToolComponent],
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
        provide: DiseaseCorrelationService,
        useValue: {
          getDiseaseCorrelations: jest.fn().mockReturnValue(of([])),
        },
      },
      ComparisonToolService,
      DiseaseCorrelationComparisonToolService,
    ],
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

import { provideHttpClient } from '@angular/common/http';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';
import { ComparisonToolConfigService } from '@sagebionetworks/agora/api-client';
import { AGORA_LOADING_ICON_COLORS } from '@sagebionetworks/agora/config';
import { ComparisonToolComponent } from '@sagebionetworks/explorers/comparison-tool';
import {
  PlatformService,
  provideComparisonToolFilterService,
  provideComparisonToolService,
  provideExplorersConfig,
} from '@sagebionetworks/explorers/services';
import { provideLoadingIconColors } from '@sagebionetworks/explorers/testing';
import { render } from '@testing-library/angular';
import { MessageService } from 'primeng/api';
import { of } from 'rxjs';
import { NominatedDrugsComparisonToolComponent } from './nominated-drugs-comparison-tool.component';
import { NominatedDrugsComparisonToolService } from './services/nominated-drugs-comparison-tool.service';

async function setup() {
  const { fixture } = await render(NominatedDrugsComparisonToolComponent, {
    imports: [ComparisonToolComponent],
    providers: [
      MessageService,
      provideLoadingIconColors(AGORA_LOADING_ICON_COLORS),
      provideExplorersConfig({ visualizationOverviewPanes: [] }),
      provideHttpClient(),
      provideNoopAnimations(),
      provideRouter([]),
      {
        provide: PlatformService,
        useValue: { isBrowser: true },
      },
      {
        provide: ComparisonToolConfigService,
        useValue: {
          getComparisonToolsConfig: jest.fn().mockReturnValue(of([])),
        },
      },
      ...provideComparisonToolService(),
      ...provideComparisonToolFilterService(),
      NominatedDrugsComparisonToolService,
    ],
  });

  const component = fixture.componentInstance;
  return { component };
}

describe('NominatedDrugsComparisonToolComponent', () => {
  it('should create', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });
});

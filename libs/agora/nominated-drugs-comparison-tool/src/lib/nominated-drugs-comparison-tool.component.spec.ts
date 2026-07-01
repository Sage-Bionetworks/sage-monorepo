import { provideHttpClient } from '@angular/common/http';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';
import {
  ComparisonToolConfigService,
  NominatedDrugService,
} from '@sagebionetworks/agora/api-client';
import {
  AGORA_LOADING_ICON_COLORS,
  NOMINATED_CTS_VISUALIZATION_OVERVIEW_PANES,
} from '@sagebionetworks/agora/config';
import { ComparisonToolComponent } from '@sagebionetworks/explorers/comparison-tool';
import { ComparisonToolQuery } from '@sagebionetworks/explorers/models';
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
  const comparisonToolService = fixture.debugElement.injector.get(
    NominatedDrugsComparisonToolService,
  );
  const nominatedDrugService = fixture.debugElement.injector.get(NominatedDrugService);
  return { component, comparisonToolService, nominatedDrugService };
}

const emptyQuery: ComparisonToolQuery = {
  categories: [],
  pinnedItems: [],
  pageNumber: 0,
  pageSize: 100,
  multiSortMeta: [],
  searchTerm: null,
  filters: [],
};

describe('NominatedDrugsComparisonToolComponent', () => {
  it('should create', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });

  it('should configure the shared nominated CTs visualization overview content', async () => {
    const { comparisonToolService } = await setup();
    expect(comparisonToolService.viewConfig().visualizationOverviewPanes).toBe(
      NOMINATED_CTS_VISUALIZATION_OVERVIEW_PANES,
    );
  });

  it('should send the selected clinical trial phase filter in the unpinned query', async () => {
    const { component, comparisonToolService, nominatedDrugService } = await setup();
    const selectedPhases = ['Phase I', 'Phase II'];
    jest.spyOn(comparisonToolService, 'selectedFilters').mockReturnValue({ phase: selectedPhases });
    const getNominatedDrugsSpy = jest
      .spyOn(nominatedDrugService, 'getNominatedDrugs')
      .mockReturnValue(of({ nominatedDrugs: [], page: { totalElements: 0 } }) as never);

    component.getUnpinnedData(emptyQuery);

    expect(getNominatedDrugsSpy).toHaveBeenCalledWith(
      expect.objectContaining({ maximumClinicalTrialPhase: selectedPhases }),
    );
  });
});

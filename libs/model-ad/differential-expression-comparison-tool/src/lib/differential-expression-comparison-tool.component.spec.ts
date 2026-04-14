import { provideHttpClient } from '@angular/common/http';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';
import { ComparisonToolComponent } from '@sagebionetworks/explorers/comparison-tool';
import {
  DEFAULT_PAGE_SIZE,
  PlatformService,
  provideComparisonToolFilterService,
  provideComparisonToolService,
  provideExplorersConfig,
} from '@sagebionetworks/explorers/services';
import { provideLoadingIconColors } from '@sagebionetworks/explorers/testing';
import {
  ComparisonToolConfigService,
  SexCohort,
  Transcriptomics,
  TranscriptomicsPage,
  TranscriptomicsService,
} from '@sagebionetworks/model-ad/api-client';
import { MODEL_AD_LOADING_ICON_COLORS } from '@sagebionetworks/model-ad/config';
import { render } from '@testing-library/angular';
import { MessageService } from 'primeng/api';
import { of } from 'rxjs';
import { DifferentialExpressionComparisonToolComponent } from './differential-expression-comparison-tool.component';
import { DifferentialExpressionComparisonToolService } from './services/differential-expression-comparison-tool.service';

const baseMockRow: Transcriptomics = {
  composite_id: 'test-id',
  ensembl_gene_id: 'ENSG00000001',
  gene_symbol: 'ABCA7',
  biodomains: [],
  name: { link_text: 'Abca7*V1599M.5xFAD', link_url: 'models/Abca7*V1599M.5xFAD' },
  matched_control: '5xFAD',
  model_group: 'Abca7*V1599M',
  model_type: 'Familial AD',
  tissue: 'Hippocampus',
  sex_cohort: SexCohort.FemalesMales,
};

function mockPage(rows: Transcriptomics[]): TranscriptomicsPage {
  return {
    transcriptomics: rows,
    page: {
      number: 0,
      size: DEFAULT_PAGE_SIZE,
      totalElements: rows.length,
      totalPages: 1,
      hasNext: false,
      hasPrevious: false,
    },
  };
}

async function setup() {
  const { fixture } = await render(DifferentialExpressionComparisonToolComponent, {
    imports: [ComparisonToolComponent],
    providers: [
      MessageService,
      provideLoadingIconColors(MODEL_AD_LOADING_ICON_COLORS),
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
          getComparisonToolConfig: jest.fn().mockReturnValue(of([])),
        },
      },
      ...provideComparisonToolService(),
      ...provideComparisonToolFilterService(),
      DifferentialExpressionComparisonToolService,
    ],
  });

  const component = fixture.componentInstance;
  const comparisonToolService = fixture.debugElement.injector.get(
    DifferentialExpressionComparisonToolService,
  );
  const transcriptomicsService = fixture.debugElement.injector.get(TranscriptomicsService);
  return { component, comparisonToolService, transcriptomicsService };
}

describe('DifferentialExpressionComparisonToolComponent', () => {
  it('should create', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });

  it('should override link_url with model_group when non-null', async () => {
    const { component, comparisonToolService, transcriptomicsService } = await setup();
    jest
      .spyOn(transcriptomicsService, 'getTranscriptomics')
      .mockReturnValue(of(mockPage([baseMockRow])) as any);
    const spy = jest.spyOn(comparisonToolService, 'setUnpinnedData');

    component.getUnpinnedData({
      pinnedItems: [],
      multiSortMeta: [],
      pageNumber: 0,
      pageSize: DEFAULT_PAGE_SIZE,
      searchTerm: '',
      categories: [],
      filters: [],
    });

    expect(spy).toHaveBeenCalledWith([
      expect.objectContaining({
        name: expect.objectContaining({ link_url: 'models/Abca7*V1599M' }),
      }),
    ]);
  });

  it('should keep original link_url when model_group is null', async () => {
    const { component, comparisonToolService, transcriptomicsService } = await setup();
    const row = {
      ...baseMockRow,
      model_group: null,
      name: { link_text: '5xFAD (UCI)', link_url: 'models/5xFAD (UCI)' },
    };
    jest
      .spyOn(transcriptomicsService, 'getTranscriptomics')
      .mockReturnValue(of(mockPage([row])) as any);
    const spy = jest.spyOn(comparisonToolService, 'setUnpinnedData');

    component.getUnpinnedData({
      pinnedItems: [],
      multiSortMeta: [],
      pageNumber: 0,
      pageSize: DEFAULT_PAGE_SIZE,
      searchTerm: '',
      categories: [],
      filters: [],
    });

    expect(spy).toHaveBeenCalledWith([
      expect.objectContaining({
        name: expect.objectContaining({ link_url: 'models/5xFAD (UCI)' }),
      }),
    ]);
  });
});

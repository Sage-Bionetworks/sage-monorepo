import { provideHttpClient } from '@angular/common/http';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';
import { ComparisonToolComponent } from '@sagebionetworks/explorers/comparison-tool';
import {
  ComparisonToolQuery,
  HeatmapCircleClickTransformFnContext,
} from '@sagebionetworks/explorers/models';
import {
  DEFAULT_PAGE_SIZE,
  PlatformService,
  provideComparisonToolFilterService,
  provideComparisonToolService,
  provideExplorersConfig,
} from '@sagebionetworks/explorers/services';
import {
  mockEmptyComparisonToolQuery,
  provideLoadingIconColors,
} from '@sagebionetworks/explorers/testing';
import {
  ComparisonToolConfigService,
  FoldChangeResult,
  Proteomics,
  ProteomicsPage,
  ProteomicsService,
  Sex,
  Transcriptomics,
  TranscriptomicsPage,
  TranscriptomicsService,
} from '@sagebionetworks/model-ad/api-client';
import { MODEL_AD_LOADING_ICON_COLORS } from '@sagebionetworks/model-ad/config';
import { render } from '@testing-library/angular';
import { MessageService } from 'primeng/api';
import { of } from 'rxjs';
import { PROTEIN_MAIN_CATEGORY, RNA_MAIN_CATEGORY } from './differential-expression-categories';
import { DifferentialExpressionComparisonToolComponent } from './differential-expression-comparison-tool.component';
import { DifferentialExpressionComparisonToolService } from './services/differential-expression-comparison-tool.service';

const UNRECOGNIZED_MAIN_CATEGORY = 'DNA - DIFFERENTIAL EXPRESSION';
const TISSUE_CATEGORY = 'Tissue - Hemibrain';

const baseMockRow: Transcriptomics = {
  composite_id: 'ENSG00000001~Abca7*V1599M.5xFAD~Female',
  ensembl_gene_id: 'ENSG00000001',
  gene_symbol: 'ABCA7',
  biodomains: [],
  name: { link_text: 'Abca7*V1599M.5xFAD', link_url: 'models/Abca7*V1599M.5xFAD' },
  matched_control: '5xFAD',
  model_group: 'Abca7*V1599M',
  model_type: 'Familial AD',
  tissue: 'Hippocampus',
  sex: Sex.Female,
};

const baseMockProteomicsRow: Proteomics = {
  composite_id: 'ENSG00000001P27144~Abca7*V1599M.5xFAD~Female',
  ensembl_gene_id: 'ENSG00000001',
  gene_symbol: 'Sptan1',
  uniprotid: 'B9EKJ1',
  unique_id: 'ENSG00000001B9EKJ1',
  display_symbol: 'Sptan1 (B9EKJ1)',
  biodomains: [],
  name: { link_text: 'Abca7*V1599M.5xFAD', link_url: 'models/Abca7*V1599M.5xFAD' },
  matched_control: '5xFAD',
  model_group: 'Abca7*V1599M',
  model_type: 'Familial AD',
  tissue: 'Hemibrain',
  sex: Sex.Female,
};

const mockCell: FoldChangeResult = { log2_fc: 1.5, adj_p_val: 0.01 };

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

function mockProteomicsPage(rows: Proteomics[]): ProteomicsPage {
  return {
    proteomics: rows,
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

function mockQuery(categories: string[]): ComparisonToolQuery {
  return { ...mockEmptyComparisonToolQuery, categories };
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
  const proteomicsService = fixture.debugElement.injector.get(ProteomicsService);

  const getTranscriptomicsSpy = jest
    .spyOn(transcriptomicsService, 'getTranscriptomics')
    .mockReturnValue(of(mockPage([baseMockRow])) as any);
  const getProteomicsSpy = jest
    .spyOn(proteomicsService, 'getProteomics')
    .mockReturnValue(of(mockProteomicsPage([baseMockProteomicsRow])) as any);

  function setMainCategory(mainCategory: string) {
    jest
      .spyOn(comparisonToolService, 'dropdownSelection')
      .mockReturnValue([mainCategory, TISSUE_CATEGORY]);
  }

  return {
    component,
    comparisonToolService,
    getTranscriptomicsSpy,
    getProteomicsSpy,
    setMainCategory,
  };
}

describe('DifferentialExpressionComparisonToolComponent', () => {
  it('should create', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });

  describe('data fetching', () => {
    it('should call the transcriptomics API for the RNA main category', async () => {
      const { component, getTranscriptomicsSpy, getProteomicsSpy } = await setup();

      component.getUnpinnedData(mockQuery([RNA_MAIN_CATEGORY, TISSUE_CATEGORY]));
      component.getPinnedData([RNA_MAIN_CATEGORY, TISSUE_CATEGORY], [], []);

      expect(getTranscriptomicsSpy).toHaveBeenCalledTimes(2);
      expect(getProteomicsSpy).not.toHaveBeenCalled();
    });

    it('should call the proteomics API for the protein main category', async () => {
      const { component, getTranscriptomicsSpy, getProteomicsSpy } = await setup();

      component.getUnpinnedData(mockQuery([PROTEIN_MAIN_CATEGORY, TISSUE_CATEGORY]));
      component.getPinnedData([PROTEIN_MAIN_CATEGORY, TISSUE_CATEGORY], [], []);

      expect(getProteomicsSpy).toHaveBeenCalledTimes(2);
      expect(getTranscriptomicsSpy).not.toHaveBeenCalled();
    });

    it.each([
      ['an unrecognized main category', [UNRECOGNIZED_MAIN_CATEGORY, TISSUE_CATEGORY]],
      ['no categories', []],
    ])('should not fetch unpinned data for %s', async (_label, categories) => {
      const { component, comparisonToolService, getTranscriptomicsSpy, getProteomicsSpy } =
        await setup();
      const setUnpinnedDataSpy = jest.spyOn(comparisonToolService, 'setUnpinnedData');

      component.getUnpinnedData(mockQuery(categories));

      expect(getTranscriptomicsSpy).not.toHaveBeenCalled();
      expect(getProteomicsSpy).not.toHaveBeenCalled();
      expect(setUnpinnedDataSpy).toHaveBeenCalledWith([]);
      expect(comparisonToolService.totalResultsCount()).toBe(0);
      expect(comparisonToolService.isLoadingTableData()).toBe(false);
    });

    it.each([
      ['an unrecognized main category', [UNRECOGNIZED_MAIN_CATEGORY, TISSUE_CATEGORY]],
      ['no categories', []],
    ])('should not fetch pinned data for %s', async (_label, categories) => {
      const { component, comparisonToolService, getTranscriptomicsSpy, getProteomicsSpy } =
        await setup();
      const setPinnedDataSpy = jest.spyOn(comparisonToolService, 'setPinnedData');

      component.getPinnedData(categories, [], []);

      expect(getTranscriptomicsSpy).not.toHaveBeenCalled();
      expect(getProteomicsSpy).not.toHaveBeenCalled();
      expect(setPinnedDataSpy).toHaveBeenCalledWith([]);
      expect(comparisonToolService.pinnedResultsCount()).toBe(0);
      expect(comparisonToolService.isLoadingTableData()).toBe(false);
    });

    it('should override link_url with model_group when non-null', async () => {
      const { component, comparisonToolService } = await setup();
      const spy = jest.spyOn(comparisonToolService, 'setUnpinnedData');

      component.getUnpinnedData(mockQuery([RNA_MAIN_CATEGORY, TISSUE_CATEGORY]));

      expect(spy).toHaveBeenCalledWith([
        expect.objectContaining({
          name: expect.objectContaining({ link_url: 'models/Abca7*V1599M' }),
        }),
      ]);
    });

    it('should override link_url with model_group for proteomics rows', async () => {
      const { component, comparisonToolService } = await setup();
      const spy = jest.spyOn(comparisonToolService, 'setUnpinnedData');

      component.getUnpinnedData(mockQuery([PROTEIN_MAIN_CATEGORY, TISSUE_CATEGORY]));

      expect(spy).toHaveBeenCalledWith([
        expect.objectContaining({
          name: expect.objectContaining({ link_url: 'models/Abca7*V1599M' }),
        }),
      ]);
    });

    it('should keep original link_url when model_group is null', async () => {
      const { component, comparisonToolService, getTranscriptomicsSpy } = await setup();
      const row = {
        ...baseMockRow,
        model_group: null,
        name: { link_text: '5xFAD (UCI)', link_url: 'models/5xFAD (UCI)' },
      };
      getTranscriptomicsSpy.mockReturnValue(of(mockPage([row])) as any);
      const spy = jest.spyOn(comparisonToolService, 'setUnpinnedData');

      component.getUnpinnedData(mockQuery([RNA_MAIN_CATEGORY, TISSUE_CATEGORY]));

      expect(spy).toHaveBeenCalledWith([
        expect.objectContaining({
          name: expect.objectContaining({ link_url: 'models/5xFAD (UCI)' }),
        }),
      ]);
    });

    it('should send the selected sex filter in the unpinned query', async () => {
      const { component, comparisonToolService, getTranscriptomicsSpy } = await setup();
      const selectedSexes = ['Female'];
      jest
        .spyOn(comparisonToolService, 'selectedFilters')
        .mockReturnValue({ sexes: selectedSexes });

      component.getUnpinnedData(mockQuery([RNA_MAIN_CATEGORY, TISSUE_CATEGORY]));

      expect(getTranscriptomicsSpy).toHaveBeenCalledWith(
        expect.objectContaining({ sex: selectedSexes }),
      );
    });
  });

  describe('selectorsWikiParams', () => {
    it('should provide wiki params for both main categories', async () => {
      const { component } = await setup();

      expect(Object.keys(component.selectorsWikiParams)).toEqual([
        RNA_MAIN_CATEGORY,
        PROTEIN_MAIN_CATEGORY,
      ]);
    });
  });

  describe('viewDetailsClick', () => {
    let windowOpenSpy: jest.SpyInstance;

    beforeEach(() => {
      windowOpenSpy = jest.spyOn(window, 'open').mockImplementation(() => null);
    });

    afterEach(() => {
      windowOpenSpy.mockRestore();
    });

    it('should open the gene details page for the RNA main category', async () => {
      const { component, setMainCategory } = await setup();
      setMainCategory(RNA_MAIN_CATEGORY);

      component.viewConfig.viewDetailsClick?.(baseMockRow);

      expect(windowOpenSpy).toHaveBeenCalledWith(
        '/genes/ENSG00000001?modelGroup=Abca7*V1599M&tissue=Hippocampus',
        '_blank',
      );
    });

    it('should open the protein details page for the protein main category', async () => {
      const { component, setMainCategory } = await setup();
      setMainCategory(PROTEIN_MAIN_CATEGORY);

      component.viewConfig.viewDetailsClick?.(baseMockProteomicsRow);

      expect(windowOpenSpy).toHaveBeenCalledWith(
        '/proteins/ENSG00000001B9EKJ1?modelGroup=Abca7*V1599M&tissue=Hemibrain',
        '_blank',
      );
    });

    it('should send the model name when model_group is null', async () => {
      const { component, setMainCategory } = await setup();
      setMainCategory(PROTEIN_MAIN_CATEGORY);

      component.viewConfig.viewDetailsClick?.({
        ...baseMockProteomicsRow,
        model_group: null,
      });

      expect(windowOpenSpy).toHaveBeenCalledWith(
        '/proteins/ENSG00000001B9EKJ1?model=Abca7*V1599M.5xFAD&tissue=Hemibrain',
        '_blank',
      );
    });

    it('should open nothing for an unrecognized main category', async () => {
      const { component, setMainCategory } = await setup();
      setMainCategory(UNRECOGNIZED_MAIN_CATEGORY);

      component.viewConfig.viewDetailsClick?.(baseMockProteomicsRow);

      expect(windowOpenSpy).not.toHaveBeenCalled();
    });
  });

  describe('heatmapCircleClickTransformFn', () => {
    function clickContext(rowData: unknown): HeatmapCircleClickTransformFnContext {
      return { rowData, cellData: mockCell, columnKey: '12 months' };
    }

    it('should build the RNA panel data', async () => {
      const { component, setMainCategory } = await setup();
      setMainCategory(RNA_MAIN_CATEGORY);

      const panelData = component.viewConfig.heatmapCircleClickTransformFn?.(
        clickContext(baseMockRow),
      );

      expect(panelData).toEqual(
        expect.objectContaining({
          heading: 'Differential RNA Expression (Hippocampus)',
          label: { left: 'ABCA7', right: 'ENSG00000001' },
          subHeadings: ['Abca7*V1599M.5xFAD (12 months, Female)', 'Matched Control: 5xFAD'],
          value: mockCell.log2_fc,
          pValue: mockCell.adj_p_val,
        }),
      );
    });

    it('should build the proteomics panel data', async () => {
      const { component, setMainCategory } = await setup();
      setMainCategory(PROTEIN_MAIN_CATEGORY);

      const panelData = component.viewConfig.heatmapCircleClickTransformFn?.(
        clickContext(baseMockProteomicsRow),
      );

      expect(panelData).toEqual(
        expect.objectContaining({
          heading: 'Differential Protein Expression (Hemibrain)',
          label: { left: 'Sptan1 (B9EKJ1)', right: 'ENSG00000001' },
          subHeadings: ['Abca7*V1599M.5xFAD (12 months, Female)', 'Matched Control: 5xFAD'],
          value: mockCell.log2_fc,
          pValue: mockCell.adj_p_val,
        }),
      );
    });

    it('should omit the right label when a proteomics row has no gene symbol', async () => {
      const { component, setMainCategory } = await setup();
      setMainCategory(PROTEIN_MAIN_CATEGORY);

      const panelData = component.viewConfig.heatmapCircleClickTransformFn?.(
        clickContext({
          ...baseMockProteomicsRow,
          gene_symbol: '',
          display_symbol: 'ENSG00000001 (B9EKJ1)',
        }),
      );

      expect(panelData?.label).toEqual({ left: 'ENSG00000001 (B9EKJ1)' });
    });

    it('should return null for an unrecognized main category', async () => {
      const { component, setMainCategory } = await setup();
      setMainCategory(UNRECOGNIZED_MAIN_CATEGORY);

      const panelData = component.viewConfig.heatmapCircleClickTransformFn?.(
        clickContext(baseMockProteomicsRow),
      );

      expect(panelData).toBeNull();
    });
  });
});

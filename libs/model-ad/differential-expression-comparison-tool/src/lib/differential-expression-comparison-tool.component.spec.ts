import { provideHttpClient } from '@angular/common/http';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';
import { ComparisonToolComponent } from '@sagebionetworks/explorers/comparison-tool';
import {
  ComparisonToolQuery,
  HeatmapCircleClickTransformFnContext,
  PinnedItemsQuery,
} from '@sagebionetworks/explorers/models';
import {
  DEFAULT_PAGE_SIZE,
  LoggerService,
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
import {
  DIFFERENTIAL_EXPRESSION_CATEGORIES,
  MODEL_AD_LOADING_ICON_COLORS,
} from '@sagebionetworks/model-ad/config';
import { render } from '@testing-library/angular';
import { MessageService } from 'primeng/api';
import { of } from 'rxjs';
import {
  DifferentialExpressionComparisonToolComponent,
  PINNED_PAGE_SIZE_PROTEIN,
  PINNED_PAGE_SIZE_RNA,
} from './differential-expression-comparison-tool.component';
import { DifferentialExpressionComparisonToolService } from './services/differential-expression-comparison-tool.service';

const UNRECOGNIZED_MAIN_CATEGORY = 'DNA - DIFFERENTIAL EXPRESSION';
const TISSUE_CATEGORY = 'Tissue - Hemibrain';
const MAIN_CATEGORIES = [
  DIFFERENTIAL_EXPRESSION_CATEGORIES.RNA,
  DIFFERENTIAL_EXPRESSION_CATEGORIES.PROTEIN,
];
const NO_PINS: PinnedItemsQuery = { items: [], itemIdSpace: 'row' };
const PINNED_ROWS_QUERY: PinnedItemsQuery = { items: ['row1', 'row2'], itemIdSpace: 'row' };
const PINNED_PARENTS_QUERY: PinnedItemsQuery = { items: ['parent1'], itemIdSpace: 'parent' };
const PREBUDGETED_PARENT_IDS = ['parent1', 'parent2'];

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
  rna_composite_id: baseMockRow.composite_id,
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

function mockPage(rows: Transcriptomics[], totalElements = rows.length): TranscriptomicsPage {
  return {
    transcriptomics: rows,
    page: {
      number: 0,
      size: DEFAULT_PAGE_SIZE,
      totalElements,
      totalPages: 1,
      hasNext: false,
      hasPrevious: false,
    },
  };
}

function mockProteomicsPage(rows: Proteomics[], totalElements = rows.length): ProteomicsPage {
  return {
    proteomics: rows,
    page: {
      number: 0,
      size: DEFAULT_PAGE_SIZE,
      totalElements,
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
      provideExplorersConfig({ tutorialPanes: [] }),
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

  const loggerErrorSpy = jest.spyOn(fixture.debugElement.injector.get(LoggerService), 'error');

  function setMainCategory(mainCategory: string) {
    jest
      .spyOn(comparisonToolService, 'dropdownSelection')
      .mockReturnValue([mainCategory, TISSUE_CATEGORY]);
  }

  function mockPageFor(
    mainCategory: string,
    {
      totalElements,
      hasRowsForPrebudgetedParents,
    }: { totalElements?: number; hasRowsForPrebudgetedParents?: boolean | null } = {},
  ) {
    if (mainCategory === DIFFERENTIAL_EXPRESSION_CATEGORIES.RNA) {
      const page = mockPage([baseMockRow], totalElements);
      getTranscriptomicsSpy.mockReturnValue(of({ ...page, hasRowsForPrebudgetedParents }) as any);
    } else {
      const page = mockProteomicsPage([baseMockProteomicsRow], totalElements);
      getProteomicsSpy.mockReturnValue(of({ ...page, hasRowsForPrebudgetedParents }) as any);
    }
  }

  function apiSpyFor(mainCategory: string) {
    return mainCategory === DIFFERENTIAL_EXPRESSION_CATEGORIES.RNA
      ? getTranscriptomicsSpy
      : getProteomicsSpy;
  }

  return {
    component,
    comparisonToolService,
    getTranscriptomicsSpy,
    getProteomicsSpy,
    loggerErrorSpy,
    setMainCategory,
    mockPageFor,
    apiSpyFor,
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

      component.getUnpinnedData(
        mockQuery([DIFFERENTIAL_EXPRESSION_CATEGORIES.RNA, TISSUE_CATEGORY]),
      );
      component.getPinnedData(
        [DIFFERENTIAL_EXPRESSION_CATEGORIES.RNA, TISSUE_CATEGORY],
        NO_PINS,
        [],
      );

      expect(getTranscriptomicsSpy).toHaveBeenCalledTimes(2);
      expect(getProteomicsSpy).not.toHaveBeenCalled();
    });

    it('should call the proteomics API for the protein main category', async () => {
      const { component, getTranscriptomicsSpy, getProteomicsSpy } = await setup();

      component.getUnpinnedData(
        mockQuery([DIFFERENTIAL_EXPRESSION_CATEGORIES.PROTEIN, TISSUE_CATEGORY]),
      );
      component.getPinnedData(
        [DIFFERENTIAL_EXPRESSION_CATEGORIES.PROTEIN, TISSUE_CATEGORY],
        NO_PINS,
        [],
      );

      expect(getProteomicsSpy).toHaveBeenCalledTimes(2);
      expect(getTranscriptomicsSpy).not.toHaveBeenCalled();
    });

    it.each([
      ['an unrecognized main category', [UNRECOGNIZED_MAIN_CATEGORY, TISSUE_CATEGORY]],
      ['no categories', []],
    ])('should not fetch unpinned data for %s', async (_label, categories) => {
      const { component, comparisonToolService, getTranscriptomicsSpy, getProteomicsSpy } =
        await setup();
      const fetchUnpinnedSpy = jest.spyOn(comparisonToolService, 'fetchUnpinned');

      component.getUnpinnedData(mockQuery(categories));

      expect(getTranscriptomicsSpy).not.toHaveBeenCalled();
      expect(getProteomicsSpy).not.toHaveBeenCalled();
      expect(fetchUnpinnedSpy).toHaveBeenCalled();
      expect(comparisonToolService.unpinnedData()).toEqual([]);
      expect(comparisonToolService.unpinnedRowCount()).toBe(0);
      expect(comparisonToolService.isLoadingTableData()).toBe(false);
    });

    it.each([
      ['an unrecognized main category', [UNRECOGNIZED_MAIN_CATEGORY, TISSUE_CATEGORY]],
      ['no categories', []],
    ])('should not fetch pinned data for %s', async (_label, categories) => {
      const { component, comparisonToolService, getTranscriptomicsSpy, getProteomicsSpy } =
        await setup();
      const fetchPinnedSpy = jest.spyOn(comparisonToolService, 'fetchPinned');

      component.getPinnedData(categories, NO_PINS, []);

      expect(getTranscriptomicsSpy).not.toHaveBeenCalled();
      expect(getProteomicsSpy).not.toHaveBeenCalled();
      expect(fetchPinnedSpy).toHaveBeenCalled();
      expect(comparisonToolService.pinnedData()).toEqual([]);
      expect(comparisonToolService.pinnedRowCount()).toBe(0);
      expect(comparisonToolService.isLoadingTableData()).toBe(false);
    });

    it('should override link_url with model_group when non-null', async () => {
      const { component, comparisonToolService } = await setup();

      component.getUnpinnedData(
        mockQuery([DIFFERENTIAL_EXPRESSION_CATEGORIES.RNA, TISSUE_CATEGORY]),
      );

      expect(comparisonToolService.unpinnedData()).toEqual([
        expect.objectContaining({
          name: expect.objectContaining({ link_url: 'models/Abca7*V1599M' }),
        }),
      ]);
    });

    it('should override link_url with model_group for proteomics rows', async () => {
      const { component, comparisonToolService } = await setup();

      component.getUnpinnedData(
        mockQuery([DIFFERENTIAL_EXPRESSION_CATEGORIES.PROTEIN, TISSUE_CATEGORY]),
      );

      expect(comparisonToolService.unpinnedData()).toEqual([
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

      component.getUnpinnedData(
        mockQuery([DIFFERENTIAL_EXPRESSION_CATEGORIES.RNA, TISSUE_CATEGORY]),
      );

      expect(comparisonToolService.unpinnedData()).toEqual([
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

      component.getUnpinnedData(
        mockQuery([DIFFERENTIAL_EXPRESSION_CATEGORIES.RNA, TISSUE_CATEGORY]),
      );

      expect(getTranscriptomicsSpy).toHaveBeenCalledWith(
        expect.objectContaining({ sex: selectedSexes }),
      );
    });
  });

  describe('parent/child pin queries', () => {
    it.each(MAIN_CATEGORIES)(
      'should send the pinned items query in the unpinned query for %s',
      async (mainCategory) => {
        const { component, comparisonToolService, apiSpyFor } = await setup();

        for (const pinnedItemsQuery of [PINNED_ROWS_QUERY, PINNED_PARENTS_QUERY]) {
          jest.spyOn(comparisonToolService, 'pinnedItemsQuery').mockReturnValue(pinnedItemsQuery);

          component.getUnpinnedData(mockQuery([mainCategory, TISSUE_CATEGORY]));

          expect(apiSpyFor(mainCategory)).toHaveBeenLastCalledWith(
            expect.objectContaining({ ...pinnedItemsQuery, itemFilterType: 'exclude' }),
          );
        }
      },
    );

    it.each(MAIN_CATEGORIES)(
      'should send the pinned items query in the pinned query for %s',
      async (mainCategory) => {
        const { component, apiSpyFor } = await setup();

        for (const pinnedItemsQuery of [PINNED_ROWS_QUERY, PINNED_PARENTS_QUERY]) {
          component.getPinnedData([mainCategory, TISSUE_CATEGORY], pinnedItemsQuery, []);

          expect(apiSpyFor(mainCategory)).toHaveBeenLastCalledWith(
            expect.objectContaining({ ...pinnedItemsQuery, itemFilterType: 'include' }),
          );
        }
      },
    );

    it.each([
      [DIFFERENTIAL_EXPRESSION_CATEGORIES.RNA, PINNED_PAGE_SIZE_RNA],
      [DIFFERENTIAL_EXPRESSION_CATEGORIES.PROTEIN, PINNED_PAGE_SIZE_PROTEIN],
    ])('should send the pinned page size for %s', async (mainCategory, pageSize) => {
      const { component, apiSpyFor } = await setup();

      component.getPinnedData([mainCategory, TISSUE_CATEGORY], PINNED_ROWS_QUERY, []);

      expect(apiSpyFor(mainCategory)).toHaveBeenCalledWith(expect.objectContaining({ pageSize }));
    });

    it.each(MAIN_CATEGORIES)(
      'should send the prebudgeted parent ids in the unpinned query for %s',
      async (mainCategory) => {
        const { component, comparisonToolService, apiSpyFor } = await setup();
        jest
          .spyOn(comparisonToolService, 'prebudgetedParentIdsForUnpinnedFetch')
          .mockReturnValue(PREBUDGETED_PARENT_IDS);

        component.getUnpinnedData(mockQuery([mainCategory, TISSUE_CATEGORY]));

        expect(apiSpyFor(mainCategory)).toHaveBeenCalledWith(
          expect.objectContaining({ prebudgetedParentIds: PREBUDGETED_PARENT_IDS }),
        );
      },
    );

    it('should not send prebudgeted parent ids in the unpinned query when there are none', async () => {
      const { component, comparisonToolService, getProteomicsSpy } = await setup();
      expect(comparisonToolService.prebudgetedParentIdsForUnpinnedFetch()).toBeUndefined();

      component.getUnpinnedData(
        mockQuery([DIFFERENTIAL_EXPRESSION_CATEGORIES.PROTEIN, TISSUE_CATEGORY]),
      );

      expect(getProteomicsSpy).toHaveBeenLastCalledWith(
        expect.not.objectContaining({ prebudgetedParentIds: expect.anything() }),
      );
    });

    it.each(MAIN_CATEGORIES)(
      'should forward hasRowsForPrebudgetedParents from the unpinned response for %s',
      async (mainCategory) => {
        const { component, comparisonToolService, mockPageFor } = await setup();
        mockPageFor(mainCategory, { hasRowsForPrebudgetedParents: true });

        component.getUnpinnedData(mockQuery([mainCategory, TISSUE_CATEGORY]));

        expect(comparisonToolService.hasRowsForPrebudgetedParents()).toBe(true);
      },
    );

    it('should send the remaining budget and prebudgeted parent ids in the pin-all query', async () => {
      const { comparisonToolService, getProteomicsSpy } = await setup();
      jest
        .spyOn(comparisonToolService, 'dropdownSelection')
        .mockReturnValue([DIFFERENTIAL_EXPRESSION_CATEGORIES.PROTEIN, TISSUE_CATEGORY]);
      jest
        .spyOn(comparisonToolService, 'query')
        .mockReturnValue(mockQuery([DIFFERENTIAL_EXPRESSION_CATEGORIES.PROTEIN, TISSUE_CATEGORY]));
      jest.spyOn(comparisonToolService, 'canPinAll').mockReturnValue(true);
      jest.spyOn(comparisonToolService, 'isChildView').mockReturnValue(true);
      jest.spyOn(comparisonToolService, 'pinnedParents').mockReturnValue(PREBUDGETED_PARENT_IDS);
      const remainingBudget = comparisonToolService.pinLimit() - comparisonToolService.pinCount();

      comparisonToolService.pinAll();

      expect(getProteomicsSpy).toHaveBeenLastCalledWith(
        expect.objectContaining({
          itemFilterType: 'exclude',
          remainingBudget,
          prebudgetedParentIds: PREBUDGETED_PARENT_IDS,
        }),
      );
    });

    it.each(MAIN_CATEGORIES)(
      'should log an error when the pinned response for %s is truncated',
      async (mainCategory) => {
        const { component, loggerErrorSpy, mockPageFor } = await setup();
        mockPageFor(mainCategory, { totalElements: 2 });

        component.getPinnedData([mainCategory, TISSUE_CATEGORY], PINNED_ROWS_QUERY, []);

        expect(loggerErrorSpy).toHaveBeenCalledWith(
          `DifferentialExpressionComparisonToolComponent: pinned ${mainCategory} fetch truncated: 2 matching rows, 1 returned`,
        );
      },
    );

    it('should not log an error when the pinned response is complete', async () => {
      const { component, loggerErrorSpy, mockPageFor } = await setup();
      mockPageFor(DIFFERENTIAL_EXPRESSION_CATEGORIES.PROTEIN, { totalElements: 1 });

      component.getPinnedData(
        [DIFFERENTIAL_EXPRESSION_CATEGORIES.PROTEIN, TISSUE_CATEGORY],
        PINNED_ROWS_QUERY,
        [],
      );

      expect(loggerErrorSpy).not.toHaveBeenCalled();
    });
  });

  describe('selectorsWikiParams', () => {
    it('should provide wiki params for both main categories', async () => {
      const { component } = await setup();

      expect(Object.keys(component.selectorsWikiParams)).toEqual([
        DIFFERENTIAL_EXPRESSION_CATEGORIES.RNA,
        DIFFERENTIAL_EXPRESSION_CATEGORIES.PROTEIN,
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
      setMainCategory(DIFFERENTIAL_EXPRESSION_CATEGORIES.RNA);

      component.viewConfig.viewDetailsClick?.(baseMockRow);

      expect(windowOpenSpy).toHaveBeenCalledWith(
        '/genes/ENSG00000001?modelGroup=Abca7*V1599M&tissue=Hippocampus',
        '_blank',
      );
    });

    it('should open the protein details page for the protein main category', async () => {
      const { component, setMainCategory } = await setup();
      setMainCategory(DIFFERENTIAL_EXPRESSION_CATEGORIES.PROTEIN);

      component.viewConfig.viewDetailsClick?.(baseMockProteomicsRow);

      expect(windowOpenSpy).toHaveBeenCalledWith(
        '/proteins/ENSG00000001B9EKJ1?modelGroup=Abca7*V1599M&tissue=Hemibrain',
        '_blank',
      );
    });

    it('should send the model name when model_group is null', async () => {
      const { component, setMainCategory } = await setup();
      setMainCategory(DIFFERENTIAL_EXPRESSION_CATEGORIES.PROTEIN);

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
      setMainCategory(DIFFERENTIAL_EXPRESSION_CATEGORIES.RNA);

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
      setMainCategory(DIFFERENTIAL_EXPRESSION_CATEGORIES.PROTEIN);

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
      setMainCategory(DIFFERENTIAL_EXPRESSION_CATEGORIES.PROTEIN);

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

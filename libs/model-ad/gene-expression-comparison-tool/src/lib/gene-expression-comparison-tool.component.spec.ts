import { provideHttpClient } from '@angular/common/http';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';
import { ComparisonToolComponent } from '@sagebionetworks/explorers/comparison-tool';
import {
  PlatformService,
  provideComparisonToolFilterService,
  provideComparisonToolService,
  provideExplorersConfig,
} from '@sagebionetworks/explorers/services';
import { provideLoadingIconColors } from '@sagebionetworks/explorers/testing';
import {
  ComparisonToolConfigService,
  GeneExpression,
  GeneExpressionService,
  GeneExpressionsPage,
  SexCohort,
} from '@sagebionetworks/model-ad/api-client';
import { MODEL_AD_LOADING_ICON_COLORS } from '@sagebionetworks/model-ad/config';
import { render } from '@testing-library/angular';
import { MessageService } from 'primeng/api';
import { of } from 'rxjs';
import { GeneExpressionComparisonToolComponent } from './gene-expression-comparison-tool.component';
import { GeneExpressionComparisonToolService } from './services/gene-expression-comparison-tool.service';

const mockRow: GeneExpression = {
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

const mockRowNoModelGroup: GeneExpression = {
  ...mockRow,
  name: { link_text: '5xFAD (UCI)', link_url: 'models/5xFAD (UCI)' },
  model_group: null,
};

async function setup() {
  const { fixture } = await render(GeneExpressionComparisonToolComponent, {
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
      GeneExpressionComparisonToolService,
    ],
  });

  const component = fixture.componentInstance;
  const comparisonToolService = fixture.debugElement.injector.get(
    GeneExpressionComparisonToolService,
  );
  const geneExpressionService = fixture.debugElement.injector.get(GeneExpressionService);
  return { component, comparisonToolService, geneExpressionService };
}

describe('GeneExpressionComparisonToolComponent', () => {
  it('should create', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });

  describe('model link override', () => {
    it('should use model_group to build link_url when model_group is non-null', async () => {
      const { component, comparisonToolService, geneExpressionService } = await setup();

      const page: GeneExpressionsPage = {
        geneExpressions: [mockRow],
        page: {
          number: 0,
          size: 10,
          totalElements: 1,
          totalPages: 1,
          hasNext: false,
          hasPrevious: false,
        },
      };
      jest.spyOn(geneExpressionService, 'getGeneExpressions').mockReturnValue(of(page) as any);
      const setUnpinnedSpy = jest.spyOn(comparisonToolService, 'setUnpinnedData');

      component.getUnpinnedData({
        pinnedItems: [],
        multiSortMeta: [],
        pageNumber: 0,
        pageSize: 10,
        searchTerm: '',
        categories: [],
        filters: [],
      });

      expect(setUnpinnedSpy).toHaveBeenCalledWith([
        expect.objectContaining({
          name: expect.objectContaining({ link_url: 'models/Abca7*V1599M' }),
        }),
      ]);
    });

    it('should leave link_url unchanged when model_group is null', async () => {
      const { component, comparisonToolService, geneExpressionService } = await setup();

      const page: GeneExpressionsPage = {
        geneExpressions: [mockRowNoModelGroup],
        page: {
          number: 0,
          size: 10,
          totalElements: 1,
          totalPages: 1,
          hasNext: false,
          hasPrevious: false,
        },
      };
      jest.spyOn(geneExpressionService, 'getGeneExpressions').mockReturnValue(of(page) as any);
      const setUnpinnedSpy = jest.spyOn(comparisonToolService, 'setUnpinnedData');

      component.getUnpinnedData({
        pinnedItems: [],
        multiSortMeta: [],
        pageNumber: 0,
        pageSize: 10,
        searchTerm: '',
        categories: [],
        filters: [],
      });

      expect(setUnpinnedSpy).toHaveBeenCalledWith([
        expect.objectContaining({
          name: expect.objectContaining({ link_url: 'models/5xFAD (UCI)' }),
        }),
      ]);
    });
  });
});

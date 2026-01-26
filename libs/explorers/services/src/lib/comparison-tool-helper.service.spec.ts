import { ComparisonToolConfig, ComparisonToolFilter } from '@sagebionetworks/explorers/models';
import {
  mockComparisonToolData,
  mockComparisonToolDataConfig,
} from '@sagebionetworks/explorers/testing';
import { ComparisonToolHelperService } from './comparison-tool-helper.service';

describe('Service: ComparisonToolHelper', () => {
  let ctHelperService: ComparisonToolHelperService;

  beforeEach(async () => {
    ctHelperService = new ComparisonToolHelperService();
  });

  it('should create filename for ct without any dropdowns', () => {
    const filename = ctHelperService.getComparisonToolDataFilename(mockComparisonToolDataConfig[0]);
    expect(filename).toBe('model_overview');
  });

  it('should create filename for ct with primary dropdown', () => {
    const config: ComparisonToolConfig = {
      ...mockComparisonToolDataConfig[0],
      page: 'Gene Expression',
      dropdowns: ['RNA - DIFFERENTIAL EXPRESSION', 'Tissue - Hemibrain', 'Sex - Females & Males'],
    };
    const filename = ctHelperService.getComparisonToolDataFilename(config);
    expect(filename).toBe('gene_expression_rna_differential_expression');
  });

  it('should reshape comparison tool data with heatmap data into one row per heatmap category', () => {
    const csvData = ctHelperService.buildComparisonToolCsvRows(
      mockComparisonToolData.slice(1, 3),
      mockComparisonToolDataConfig[0],
      'https://www.modeladexplorer.org',
    );
    expect(csvData).toEqual([
      [
        'name',
        'cluster',
        'model_type',
        'matched_controls',
        'disease_correlation',
        'center',
        'age',
        'heatmap',
        'correlation',
        'adj_p_val',
        'modified_genes',
      ],
      [
        '5xFAD (UCI)',
        'Cluster A',
        'Familial AD',
        'C57BL/6J',
        '',
        'http://model-ad.org/uci-disease-model-development-and-phenotyping-dmp/',
        '12 months',
        'IFG',
        '0.180202547119964',
        '0.107436343673528',
        'APP,Psen1',
      ],
      [
        '5xFAD (UCI)',
        'Cluster A',
        'Familial AD',
        'C57BL/6J',
        '',
        'http://model-ad.org/uci-disease-model-development-and-phenotyping-dmp/',
        '12 months',
        'PHG',
        '0.120000548192961',
        '0.239210931286464',
        'APP,Psen1',
      ],
      [
        '5xFAD (IU/Jax/Pitt)',
        'Cluster A',
        'Familial AD',
        'C57BL/6J',
        'https://www.modeladexplorer.org/comparison/correlation?model=5xFAD (IU/Jax/Pitt)',
        'https://www.model-ad.org/iu-jax-pitt-disease-modeling-project/',
        '4 months',
        'IFG',
        '0.0829228557717284',
        '0.46174597641461',
        'APP,Psen1',
      ],
      [
        '5xFAD (IU/Jax/Pitt)',
        'Cluster A',
        'Familial AD',
        'C57BL/6J',
        'https://www.modeladexplorer.org/comparison/correlation?model=5xFAD (IU/Jax/Pitt)',
        'https://www.model-ad.org/iu-jax-pitt-disease-modeling-project/',
        '4 months',
        'PHG',
        '0.0555691060937462',
        '0.586807542376288',
        'APP,Psen1',
      ],
    ]);
  });

  it('should reshape comparison tool data without heatmap data', () => {
    const data: Record<string, any>[] = [
      { name: '3xTg-AD', column_1: 'Value 1' },
      { name: '5xFAD (UCI)', column_1: 'Value 2' },
    ];

    const config: ComparisonToolConfig = {
      page: 'Model Overview',
      dropdowns: [],
      row_count: 'over 200000',
      columns: [
        { type: 'primary', data_key: 'name', is_exported: true, is_hidden: false },
        {
          name: 'Column 1',
          type: 'text',
          data_key: 'column_1',
          is_exported: true,
          is_hidden: false,
        },
      ],
      filters: [],
    };

    const csvData = ctHelperService.buildComparisonToolCsvRows(
      data,
      config,
      'https://www.modeladexplorer.org',
    );
    expect(csvData).toEqual([
      ['name', 'column_1'],
      ['3xTg-AD', 'Value 1'],
      ['5xFAD (UCI)', 'Value 2'],
    ]);
  });

  describe('getSelectedFilters', () => {
    it('should return empty object when no filters have selected options', () => {
      const filters: ComparisonToolFilter[] = [
        {
          name: 'Filter 1',
          data_key: 'field1',
          query_param_key: 'field1',
          options: [
            { label: 'Option A', selected: false },
            { label: 'Option B', selected: false },
          ],
        },
        {
          name: 'Filter 2',
          data_key: 'field2',
          query_param_key: 'field2',
          options: [{ label: 'Option C', selected: false }],
        },
      ];

      expect(ctHelperService.getSelectedFilters(filters)).toEqual({});
    });

    it('should return selected option labels keyed by query_param_key', () => {
      const filters: ComparisonToolFilter[] = [
        {
          name: 'Species',
          data_key: 'species',
          query_param_key: 'species',
          options: [
            { label: 'Mouse', selected: true },
            { label: 'Human', selected: false },
            { label: 'Rat', selected: true },
          ],
        },
        {
          name: 'Status',
          data_key: 'status',
          query_param_key: 'statuses',
          options: [
            { label: 'Active', selected: true },
            { label: 'Inactive', selected: false },
          ],
        },
      ];

      expect(ctHelperService.getSelectedFilters(filters)).toEqual({
        species: ['Mouse', 'Rat'],
        statuses: ['Active'],
      });
    });

    it('should exclude filters with no selected options', () => {
      const filters: ComparisonToolFilter[] = [
        {
          name: 'Filter With Selection',
          data_key: 'withSelection',
          query_param_key: 'withSelection',
          options: [{ label: 'Selected', selected: true }],
        },
        {
          name: 'Filter Without Selection',
          data_key: 'withoutSelection',
          query_param_key: 'withoutSelection',
          options: [{ label: 'Not Selected', selected: false }],
        },
      ];

      const result = ctHelperService.getSelectedFilters(filters);
      expect(result).toEqual({ withSelection: ['Selected'] });
      expect(result['withoutSelection']).toBeUndefined();
    });

    it('should return empty object when filters array is empty', () => {
      expect(ctHelperService.getSelectedFilters([])).toEqual({});
    });
  });
});

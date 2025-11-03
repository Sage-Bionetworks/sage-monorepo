import { ComparisonToolConfig } from '@sagebionetworks/explorers/models';
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

  it('should reshape comparison tool data with heatmap data into one row per heatmap category', () => {
    const csvData = ctHelperService.reshapeComparisonToolDataToStringArray(
      mockComparisonToolData.slice(1, 3),
      mockComparisonToolDataConfig[0],
      'https://www.modeladexplorer.org',
      'human_gene_module',
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
        'human_gene_module',
        'correlation',
        'adj_p_val',
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

    const csvData = ctHelperService.reshapeComparisonToolDataToStringArray(
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
});

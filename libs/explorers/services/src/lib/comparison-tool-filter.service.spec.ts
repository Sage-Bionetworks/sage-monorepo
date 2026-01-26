import { TestBed } from '@angular/core/testing';
import { ComparisonToolFilter } from '@sagebionetworks/explorers/models';
import { ComparisonToolFilterService } from './comparison-tool-filter.service';
import { provideComparisonToolFilterService } from './comparison-tool-filter.service.providers';
import { ComparisonToolService } from './comparison-tool.service';
import { provideComparisonToolService } from './comparison-tool.service.providers';

describe('ComparisonToolFilterService', () => {
  let service: ComparisonToolFilterService;
  let comparisonToolService: ComparisonToolService<Record<string, unknown>>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [...provideComparisonToolService(), ...provideComparisonToolFilterService()],
    });
    service = TestBed.inject(ComparisonToolFilterService);
    comparisonToolService = TestBed.inject(ComparisonToolService);
  });

  it('should set filters', () => {
    const filters: ComparisonToolFilter[] = [
      { name: 'Filter 1', data_key: 'f1', query_param_key: 'f1', options: [] },
      { name: 'Filter 2', data_key: 'f2', query_param_key: 'f2', options: [] },
    ];
    service.setFilters(filters);
    expect(service.filters()).toEqual(filters);
  });

  it('should reset page to 0 when filters are changed', () => {
    comparisonToolService.updateQuery({ pageNumber: 5 });
    expect(comparisonToolService.pageNumber()).toBe(5);

    const filters: ComparisonToolFilter[] = [
      {
        name: 'Test Filter',
        data_key: 'testField',
        query_param_key: 'testField',
        options: [
          { label: 'Option 1', selected: true },
          { label: 'Option 2', selected: false },
        ],
      },
    ];
    service.setFilters(filters);

    expect(comparisonToolService.pageNumber()).toBe(0);
  });

  it('should reset page to 0 when search term is updated', () => {
    comparisonToolService.updateQuery({ pageNumber: 3 });
    expect(comparisonToolService.pageNumber()).toBe(3);

    service.updateSearchTerm('test search');

    expect(comparisonToolService.pageNumber()).toBe(0);
    expect(service.searchTerm()).toBe('test search');
  });
});

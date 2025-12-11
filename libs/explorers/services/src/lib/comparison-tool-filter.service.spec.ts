import { TestBed } from '@angular/core/testing';
import { ComparisonToolFilter } from '@sagebionetworks/explorers/models';
import { ComparisonToolFilterService } from './comparison-tool-filter.service';
import { provideComparisonToolFilterService } from './comparison-tool-filter.service.providers';
import { provideComparisonToolService } from './comparison-tool.service.providers';

describe('ComparisonToolFilterService', () => {
  let service: ComparisonToolFilterService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [...provideComparisonToolService(), ...provideComparisonToolFilterService()],
    });
    service = TestBed.inject(ComparisonToolFilterService);
  });

  it('should set filters', () => {
    const filters: ComparisonToolFilter[] = [
      { name: 'Filter 1', data_key: 'f1', options: [] },
      { name: 'Filter 2', data_key: 'f2', options: [] },
    ];
    service.setFilters(filters);
    expect(service.filters()).toEqual(filters);
  });
});

import { TestBed } from '@angular/core/testing';
import { ComparisonToolFilterService } from './comparison-tool-filter.service';
import { ComparisonToolFilter } from '@sagebionetworks/explorers/models';

describe('ComparisonToolFilterService', () => {
  let service: ComparisonToolFilterService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ComparisonToolFilterService);
  });

  it('should set filters', () => {
    const filters: ComparisonToolFilter[] = [
      { name: 'Filter 1', field: 'f1', options: [] },
      { name: 'Filter 2', field: 'f2', options: [] },
    ];
    service.setFilters(filters);
    expect(service.filters()).toEqual(filters);
  });
});

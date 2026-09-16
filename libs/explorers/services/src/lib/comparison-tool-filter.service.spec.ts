import { TestBed } from '@angular/core/testing';
import { ComparisonToolFilter } from '@sagebionetworks/explorers/models';
import { ComparisonToolFilterService } from './comparison-tool-filter.service';
import { provideComparisonToolFilterService } from './comparison-tool-filter.service.providers';
import { ComparisonToolService } from './comparison-tool.service';
import { provideComparisonToolService } from './comparison-tool.service.providers';
import { MessageService } from 'primeng/api';

describe('ComparisonToolFilterService', () => {
  let service: ComparisonToolFilterService;
  let comparisonToolService: ComparisonToolService<Record<string, unknown>>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        MessageService,
        ...provideComparisonToolService(),
        ...provideComparisonToolFilterService(),
      ],
    });
    service = TestBed.inject(ComparisonToolFilterService);
    comparisonToolService = TestBed.inject(ComparisonToolService);
  });

  it('should update the targeted option and reset page to 0 when a filter option is toggled', () => {
    const filters: ComparisonToolFilter[] = [
      {
        name: 'Test Filter',
        query_param_key: 'testField',
        options: [
          { label: 'Option 1', selected: false },
          { label: 'Option 2', selected: false },
        ],
      },
    ];
    comparisonToolService.updateQuery({ filters, pageNumber: 5 });
    expect(comparisonToolService.pageNumber()).toBe(5);

    service.setFilterOptionSelected('testField', 'Option 1', true);

    const options = service.filters()[0].options;
    expect(options.find((option) => option.label === 'Option 1')?.selected).toBe(true);
    expect(options.find((option) => option.label === 'Option 2')?.selected).toBe(false);
    expect(comparisonToolService.pageNumber()).toBe(0);
  });

  it('should deselect every option and reset page to 0 when all filters are cleared', () => {
    const filters: ComparisonToolFilter[] = [
      {
        name: 'Test Filter',
        query_param_key: 'testField',
        options: [
          { label: 'Option 1', selected: true },
          { label: 'Option 2', selected: true },
        ],
      },
    ];
    comparisonToolService.updateQuery({ filters, pageNumber: 5 });
    expect(comparisonToolService.pageNumber()).toBe(5);

    service.clearAllFilters();

    expect(service.filters()[0].options.every((option) => !option.selected)).toBe(true);
    expect(comparisonToolService.pageNumber()).toBe(0);
  });

  it('should not reset page when clearing filters that are already all deselected', () => {
    const filters: ComparisonToolFilter[] = [
      {
        name: 'Test Filter',
        query_param_key: 'testField',
        options: [
          { label: 'Option 1', selected: false },
          { label: 'Option 2', selected: false },
        ],
      },
    ];
    comparisonToolService.updateQuery({ filters, pageNumber: 5 });
    expect(comparisonToolService.pageNumber()).toBe(5);

    service.clearAllFilters();

    expect(comparisonToolService.pageNumber()).toBe(5);
    expect(service.filters()[0].options.every((option) => !option.selected)).toBe(true);
  });

  it('should not reset page when toggling an option to its current state', () => {
    const filters: ComparisonToolFilter[] = [
      {
        name: 'Test Filter',
        query_param_key: 'testField',
        options: [
          { label: 'Option 1', selected: false },
          { label: 'Option 2', selected: false },
        ],
      },
    ];
    comparisonToolService.updateQuery({ filters, pageNumber: 5 });
    expect(comparisonToolService.pageNumber()).toBe(5);

    service.setFilterOptionSelected('testField', 'Option 1', false);

    expect(comparisonToolService.pageNumber()).toBe(5);
    expect(
      service.filters()[0].options.find((option) => option.label === 'Option 1')?.selected,
    ).toBe(false);
  });

  it('should reset page to 0 when search term is updated', () => {
    comparisonToolService.updateQuery({ pageNumber: 3 });
    expect(comparisonToolService.pageNumber()).toBe(3);

    service.updateSearchTerm('test search');

    expect(comparisonToolService.pageNumber()).toBe(0);
    expect(service.searchTerm()).toBe('test search');
  });
});

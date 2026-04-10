import { signal } from '@angular/core';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import {
  ComparisonToolService,
  DEFAULT_PAGE_SIZE,
  provideExplorersConfig,
} from '@sagebionetworks/explorers/services';
import { render } from '@testing-library/angular';
import { ComparisonToolFooterComponent } from './comparison-tool-footer.component';

function getMockService(
  totalResults = 0,
  pageSize = DEFAULT_PAGE_SIZE,
  pageNumber = 0,
  isInitialized = true,
) {
  return {
    totalResultsCount: signal(totalResults),
    pageSize: signal(pageSize),
    first: signal(pageNumber * pageSize),
    updateQuery: jest.fn(),
    isLegendVisible: signal(false),
    isVisualizationOverviewVisible: signal(false),
    setLegendVisibility: jest.fn(),
    setVisualizationOverviewVisibility: jest.fn(),
    currentConfig: signal(null),
    viewConfig: signal({
      helpLinks: [],
      legendPanelConfig: {
        colorChartLowerLabel: '',
        colorChartUpperLabel: '',
        colorChartText: '',
        sizeChartLowerLabel: '',
        sizeChartUpperLabel: '',
        sizeChartText: '',
      },
    }),
    unpinnedData: signal([]),
    isInitialized: signal(isInitialized),
  };
}

async function setup(
  totalResults = 0,
  pageSize = DEFAULT_PAGE_SIZE,
  pageNumber = 0,
  isInitialized = true,
) {
  const mockService = getMockService(totalResults, pageSize, pageNumber, isInitialized);
  const { fixture } = await render(ComparisonToolFooterComponent, {
    providers: [
      provideNoopAnimations(),
      provideExplorersConfig({ visualizationOverviewPanes: [] }),
      { provide: ComparisonToolService, useValue: mockService },
    ],
  });
  const component = fixture.componentInstance;
  return { component, fixture, mockService };
}

describe('ComparisonToolFooterComponent', () => {
  it('should create', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });

  it('should not show paginator when not initialized', async () => {
    const { fixture } = await setup(0, 10, 0, false);
    expect(fixture.nativeElement.querySelector('p-paginator')).toBeNull();
  });

  it('should show paginator when initialized even with 0 results', async () => {
    const { fixture } = await setup(0);
    expect(fixture.nativeElement.querySelector('p-paginator')).toBeTruthy();
  });

  it('should show paginator when initialized with results', async () => {
    const { fixture } = await setup(25);
    expect(fixture.nativeElement.querySelector('p-paginator')).toBeTruthy();
  });

  it('should compute shouldPaginate based on isInitialized', async () => {
    const { component, mockService } = await setup(0, 10, 0, false);
    expect(component.shouldPaginate()).toBe(false);
    mockService.isInitialized.set(true);
    expect(component.shouldPaginate()).toBe(true);
  });

  it('should call updateQuery with correct pageNumber and pageSize on page change', async () => {
    const { component, mockService } = await setup(100);
    component.onPageChange({ first: 20, rows: 10, page: 2, pageCount: 10 });
    expect(mockService.updateQuery).toHaveBeenCalledWith({ pageNumber: 2, pageSize: 10 });
  });

  it('should handle page size change', async () => {
    const { component, mockService } = await setup(100);
    component.onPageChange({ first: 0, rows: 25, page: 0, pageCount: 4 });
    expect(mockService.updateQuery).toHaveBeenCalledWith({ pageNumber: 0, pageSize: 25 });
  });

  it('should use current pageSize when rows is not in event', async () => {
    const { component, mockService } = await setup(100, 10);
    component.onPageChange({ first: 30 } as any);
    expect(mockService.updateQuery).toHaveBeenCalledWith({ pageNumber: 3, pageSize: 10 });
  });

  it('should default to page 0 when first is null', async () => {
    const { component, mockService } = await setup(100);
    component.onPageChange({ first: null, rows: 10 } as any);
    expect(mockService.updateQuery).toHaveBeenCalledWith({ pageNumber: 0, pageSize: 10 });
  });
});

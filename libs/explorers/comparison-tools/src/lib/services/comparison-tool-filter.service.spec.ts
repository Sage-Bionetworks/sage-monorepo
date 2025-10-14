import { FilterPanelService } from './comparison-tool-filter.service';
import { ComparisonToolFilter } from '@sagebionetworks/explorers/models';

describe('FilterPanelService', () => {
  let service: FilterPanelService;

  beforeEach(() => {
    service = new FilterPanelService();
  });

  it('should be closed by default', () => {
    expect(service.isOpen()).toBe(false);
  });

  it('should open the panel', () => {
    service.open();
    expect(service.isOpen()).toBe(true);
  });

  it('should close the panel', () => {
    service.open();
    service.close();
    expect(service.isOpen()).toBe(false);
    expect(service.activePane()).toBe(-1);
  });

  it('should toggle the panel open/close', () => {
    service.toggle();
    expect(service.isOpen()).toBe(true);
    service.toggle();
    expect(service.isOpen()).toBe(false);
  });

  it('should reset activePane to -1 when closing', () => {
    service.setActivePane(2);
    service.open();
    service.toggle(); // closes
    expect(service.activePane()).toBe(-1);
  });

  it('should set activePane', () => {
    service.setActivePane(3);
    expect(service.activePane()).toBe(3);
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

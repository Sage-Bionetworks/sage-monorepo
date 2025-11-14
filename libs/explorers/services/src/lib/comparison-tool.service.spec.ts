import { TestBed } from '@angular/core/testing';
import { ComparisonToolColumn, ComparisonToolConfig } from '@sagebionetworks/explorers/models';
import { mockComparisonToolDataConfig } from '@sagebionetworks/explorers/testing';
import { FilterService, MessageService } from 'primeng/api';
import { ComparisonToolService } from './comparison-tool.service';
import { provideComparisonToolService } from './comparison-tool.service.providers';

describe('ComparisonToolService', () => {
  let service: ComparisonToolService<Record<string, unknown>>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [FilterService, ...provideComparisonToolService(), MessageService],
    });
    service = TestBed.inject(ComparisonToolService);
  });

  it('excludes hidden columns from columns and cached dropdown columns', () => {
    service.initialize(mockComparisonToolDataConfig);

    const columns = service.columns();
    expect(columns.every((column) => !column.is_hidden)).toBe(true);
    expect(columns.find((column) => column.data_key === 'available_data')).toBeUndefined();

    const columnsForDropdowns: Map<string, ComparisonToolColumn[]> = (
      service as any
    ).columnsForDropdownsSignal();
    const cachedColumns = columnsForDropdowns.get(JSON.stringify([]));

    expect(cachedColumns).toBeDefined();
    expect(cachedColumns?.find((column) => column.data_key === 'available_data')).toBeUndefined();
    expect(cachedColumns?.every((column) => column.selected)).toBe(true);
  });

  describe('pinned items cache', () => {
    const mockConfigs: ComparisonToolConfig[] = [
      {
        ...mockComparisonToolDataConfig[0],
        dropdowns: ['category1', 'option1'],
      },
      {
        ...mockComparisonToolDataConfig[0],
        dropdowns: ['category1', 'option2'],
      },
      {
        ...mockComparisonToolDataConfig[0],
        dropdowns: ['category2', 'option1'],
      },
    ];

    beforeEach(() => {
      service.initialize(mockConfigs, ['category1', 'option1']);
    });

    it('should persist pinned items when switching between dropdown selections', () => {
      // Pin items for first selection
      service.pinItem('item1');
      service.pinItem('item2');
      expect(service.pinnedItems().size).toBe(2);
      expect(service.isPinned('item1')).toBe(true);
      expect(service.isPinned('item2')).toBe(true);

      // Switch to second selection
      service.setDropdownSelection(['category1', 'option2']);
      // Should start with no pinned items for new selection
      expect(service.pinnedItems().size).toBe(0);
      expect(service.isPinned('item1')).toBe(false);

      // Pin different items
      service.pinItem('item3');
      expect(service.pinnedItems().size).toBe(1);
      expect(service.isPinned('item3')).toBe(true);

      // Switch back to first selection
      service.setDropdownSelection(['category1', 'option1']);
      // Should restore originally pinned items
      expect(service.pinnedItems().size).toBe(2);
      expect(service.isPinned('item1')).toBe(true);
      expect(service.isPinned('item2')).toBe(true);
      expect(service.isPinned('item3')).toBe(false);

      // Switch to second selection again
      service.setDropdownSelection(['category1', 'option2']);
      // Should restore pinned items from second selection
      expect(service.pinnedItems().size).toBe(1);
      expect(service.isPinned('item3')).toBe(true);
      expect(service.isPinned('item1')).toBe(false);
    });

    it('should handle unpinning items and persist changes', () => {
      // Pin and unpin items
      service.pinItem('item1');
      service.pinItem('item2');
      service.unpinItem('item1');
      expect(service.pinnedItems().size).toBe(1);
      expect(service.isPinned('item2')).toBe(true);

      // Switch and come back
      service.setDropdownSelection(['category1', 'option2']);
      service.setDropdownSelection(['category1', 'option1']);

      // Should remember that item1 was unpinned
      expect(service.pinnedItems().size).toBe(1);
      expect(service.isPinned('item1')).toBe(false);
      expect(service.isPinned('item2')).toBe(true);
    });

    it('should clear pinned items cache when initialize is called', () => {
      // Pin items
      service.pinItem('item1');
      service.setDropdownSelection(['category1', 'option2']);
      service.pinItem('item2');

      // Re-initialize
      service.initialize(mockConfigs, ['category1', 'option1']);

      // Cache should be cleared
      expect(service.pinnedItems().size).toBe(0);

      // Switch to second selection - should not restore old pinned items
      service.setDropdownSelection(['category1', 'option2']);
      expect(service.pinnedItems().size).toBe(0);
    });

    it('should handle toggling pins correctly', () => {
      service.togglePin('item1');
      expect(service.isPinned('item1')).toBe(true);

      service.togglePin('item1');
      expect(service.isPinned('item1')).toBe(false);

      // Pin again and switch selections
      service.togglePin('item1');
      expect(service.isPinned('item1')).toBe(true);

      service.setDropdownSelection(['category1', 'option2']);
      service.setDropdownSelection(['category1', 'option1']);

      // Should still be pinned after switching back
      expect(service.isPinned('item1')).toBe(true);
    });

    it('should handle pinning multiple items at once', () => {
      service.pinList(['item1', 'item2', 'item3']);
      expect(service.pinnedItems().size).toBe(3);

      // Switch and return
      service.setDropdownSelection(['category2', 'option1']);
      service.setDropdownSelection(['category1', 'option1']);

      // Should restore all pinned items
      expect(service.pinnedItems().size).toBe(3);
      expect(service.isPinned('item1')).toBe(true);
      expect(service.isPinned('item2')).toBe(true);
      expect(service.isPinned('item3')).toBe(true);
    });
  });
});

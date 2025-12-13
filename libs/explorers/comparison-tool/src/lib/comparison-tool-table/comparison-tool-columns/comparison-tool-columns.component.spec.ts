import { RouterModule } from '@angular/router';
import {
  ComparisonToolService,
  ComparisonToolServiceOptions,
  provideComparisonToolService,
} from '@sagebionetworks/explorers/services';
import {
  mockComparisonToolColumns,
  mockComparisonToolConfigs,
} from '@sagebionetworks/explorers/testing';
import { render, screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { SortMeta } from 'primeng/api';
import { ComparisonToolColumnsComponent } from './comparison-tool-columns.component';

async function setup(options?: Partial<ComparisonToolServiceOptions>) {
  const user = userEvent.setup();
  const component = await render(ComparisonToolColumnsComponent, {
    imports: [RouterModule],
    providers: [
      ...provideComparisonToolService({ configs: mockComparisonToolConfigs, ...options }),
    ],
    componentInputs: {},
  });
  const fixture = component.fixture;
  const service = fixture.debugElement.injector.get(ComparisonToolService);

  return { component, fixture, service, user };
}

describe('ComparisonToolColumnsComponent', () => {
  it('should create the component', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });

  it('should display column headers', async () => {
    await setup();
    const namedColumns = mockComparisonToolColumns.filter((column) => column.name);
    for (const column of namedColumns) {
      expect(screen.getByRole('columnheader', { name: column.name })).toBeVisible();
    }
  });

  it('should display column header tooltips', async () => {
    const { user } = await setup();
    const namedColumnsWithTooltips = mockComparisonToolColumns.filter(
      (column) => column.name && column.tooltip,
    );
    for (const namedColumnWithTooltip of namedColumnsWithTooltips) {
      const column = screen.getByRole('columnheader', { name: namedColumnWithTooltip.name });
      const span = column.querySelector('span') as HTMLElement;
      await user.hover(span);
      expect(screen.getByRole('tooltip', { name: namedColumnWithTooltip.tooltip })).toBeVisible();
    }
  });

  it('should display column header sort tooltips', async () => {
    const { user } = await setup();
    const namedColumnsWithSortTooltips = mockComparisonToolColumns.filter(
      (column) => column.name && column.sort_tooltip,
    );
    for (const namedColumnWithSortTooltip of namedColumnsWithSortTooltips) {
      const column = screen.getByRole('columnheader', { name: namedColumnWithSortTooltip.name });
      const span = column.querySelector('p-sorticon') as HTMLElement;
      await user.hover(span);
      expect(
        screen.getByRole('tooltip', { name: namedColumnWithSortTooltip.sort_tooltip }),
      ).toBeVisible();
    }
  });

  it('should update sort state when column sorts are clicked', async () => {
    const { user, service } = await setup();
    expect(service.multiSortMeta()).toStrictEqual([]);

    const firstColumnToSort = mockComparisonToolColumns[0];
    const firstColumnHeader = screen.getByRole('columnheader', { name: firstColumnToSort.name });

    await user.click(firstColumnHeader);
    expect(service.multiSortMeta()).toEqual([{ field: firstColumnToSort.data_key, order: -1 }]);

    await user.click(firstColumnHeader);
    expect(service.multiSortMeta()).toEqual([{ field: firstColumnToSort.data_key, order: 1 }]);

    const secondColumnToSort = mockComparisonToolColumns[1];
    const secondColumnHeader = screen.getByRole('columnheader', { name: secondColumnToSort.name });

    await user.click(secondColumnHeader);
    expect(service.multiSortMeta()).toEqual([{ field: secondColumnToSort.data_key, order: -1 }]);
  });

  it('should allow sorting by multiple columns when metaKey is held', async () => {
    const { user, service } = await setup();
    expect(service.multiSortMeta()).toStrictEqual([]);

    const firstColumnToSort = mockComparisonToolColumns[0];
    const firstColumnHeader = screen.getByRole('columnheader', { name: firstColumnToSort.name });

    await user.click(firstColumnHeader);
    expect(service.multiSortMeta()).toEqual([{ field: firstColumnToSort.data_key, order: -1 }]);

    const secondColumnToSort = mockComparisonToolColumns[1];
    const secondColumnHeader = screen.getByRole('columnheader', { name: secondColumnToSort.name });

    await user.keyboard('{Meta>}');
    await user.click(secondColumnHeader);
    await user.keyboard('{/Meta}');
    expect(service.multiSortMeta()).toEqual([
      { field: firstColumnToSort.data_key, order: -1 },
      { field: secondColumnToSort.data_key, order: -1 },
    ]);
  });

  describe('sort indicators', () => {
    it('should display sort icon on column with default sort', async () => {
      const defaultSort: SortMeta[] = [{ field: mockComparisonToolColumns[0].data_key, order: -1 }];
      await setup({ multiSortMeta: defaultSort });

      const columnHeader = screen.getByRole('columnheader', {
        name: mockComparisonToolColumns[0].name,
      });
      // Column should have the sorted class
      expect(columnHeader).toHaveClass('p-datatable-column-sorted');
      // Should have descending sort indicator
      expect(columnHeader).toHaveAttribute('aria-sort', 'descending');
    });

    it('should display sort order numbers for multi-column sort', async () => {
      const defaultSort: SortMeta[] = [
        { field: mockComparisonToolColumns[0].data_key, order: -1 },
        { field: mockComparisonToolColumns[1].data_key, order: 1 },
      ];
      await setup({ multiSortMeta: defaultSort });

      // PrimeNG includes badge number in accessible name for multi-column sort
      const firstColumnHeader = screen.getByRole('columnheader', {
        name: new RegExp(`^${mockComparisonToolColumns[0].name}\\s+1$`),
      });
      const secondColumnHeader = screen.getByRole('columnheader', {
        name: new RegExp(`^${mockComparisonToolColumns[1].name}\\s+2$`),
      });

      const firstSortBadge = firstColumnHeader.querySelector('.p-sortable-column-badge');
      const secondSortBadge = secondColumnHeader.querySelector('.p-sortable-column-badge');

      expect(firstSortBadge).toHaveTextContent('1');
      expect(secondSortBadge).toHaveTextContent('2');

      // First column should be descending, second ascending
      expect(firstColumnHeader).toHaveAttribute('aria-sort', 'descending');
      expect(secondColumnHeader).toHaveAttribute('aria-sort', 'ascending');
    });

    it('should not display sort indicator on unsorted columns', async () => {
      const defaultSort: SortMeta[] = [{ field: mockComparisonToolColumns[0].data_key, order: -1 }];
      await setup({ multiSortMeta: defaultSort });

      // Unsorted columns should not have the sorted class
      const unsortedColumnHeader = screen.getByRole('columnheader', {
        name: mockComparisonToolColumns[1].name,
      });
      expect(unsortedColumnHeader).not.toHaveClass('p-datatable-column-sorted');
      expect(unsortedColumnHeader).toHaveAttribute('aria-sort', 'none');
    });

    it('should update sort indicators when sort changes', async () => {
      const { user, service } = await setup();

      const firstColumn = mockComparisonToolColumns[0];
      const firstColumnHeader = screen.getByRole('columnheader', { name: firstColumn.name });

      // Initially no sort
      expect(firstColumnHeader).not.toHaveClass('p-datatable-column-sorted');
      expect(firstColumnHeader).toHaveAttribute('aria-sort', 'none');

      // Click to sort
      await user.click(firstColumnHeader);

      // Now should be sorted descending
      expect(firstColumnHeader).toHaveClass('p-datatable-column-sorted');
      expect(firstColumnHeader).toHaveAttribute('aria-sort', 'descending');
      expect(service.multiSortMeta()).toEqual([{ field: firstColumn.data_key, order: -1 }]);

      // Click again to toggle to ascending
      await user.click(firstColumnHeader);
      expect(firstColumnHeader).toHaveAttribute('aria-sort', 'ascending');
      expect(service.multiSortMeta()).toEqual([{ field: firstColumn.data_key, order: 1 }]);
    });
  });
});

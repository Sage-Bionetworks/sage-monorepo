import { RouterModule } from '@angular/router';
import {
  ComparisonToolService,
  provideComparisonToolService,
} from '@sagebionetworks/explorers/services';
import {
  mockComparisonToolColumns,
  mockComparisonToolConfigs,
} from '@sagebionetworks/explorers/testing';
import { render, screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { ComparisonToolColumnsComponent } from './comparison-tool-columns.component';

async function setup() {
  const user = userEvent.setup();
  const component = await render(ComparisonToolColumnsComponent, {
    imports: [RouterModule],
    providers: [...provideComparisonToolService({ configs: mockComparisonToolConfigs })],
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
    for (const columnConfig of namedColumns) {
      expect(screen.getByRole('columnheader', { name: columnConfig.name })).toBeVisible();
    }
  });

  it('should display column header tooltips', async () => {
    const { user } = await setup();
    const namedColumnsWithTooltips = mockComparisonToolColumns.filter(
      (column) => column.name && column.tooltip,
    );
    for (const columnConfig of namedColumnsWithTooltips) {
      const column = screen.getByRole('columnheader', { name: columnConfig.name });
      const span = column.querySelector('span') as HTMLElement;
      await user.hover(span);
      expect(screen.getByRole('tooltip', { name: columnConfig.tooltip })).toBeVisible();
    }
  });

  it('should display column header sort tooltips', async () => {
    const { user } = await setup();
    const namedColumnsWithSortTooltips = mockComparisonToolColumns.filter(
      (column) => column.name && column.sort_tooltip,
    );
    for (const columnConfig of namedColumnsWithSortTooltips) {
      const column = screen.getByRole('columnheader', { name: columnConfig.name });
      const span = column.querySelector('p-sorticon') as HTMLElement;
      await user.hover(span);
      expect(screen.getByRole('tooltip', { name: columnConfig.sort_tooltip })).toBeVisible();
    }
  });

  it('should update sort state when column sorts are clicked', async () => {
    const { user, service } = await setup();
    expect(service.sortField()).toBe(undefined);
    expect(service.sortOrder()).toBe(-1);

    const firstColumnToSort = mockComparisonToolColumns[0];
    const firstColumnHeader = screen.getByRole('columnheader', { name: firstColumnToSort.name });

    await user.click(firstColumnHeader);
    expect(service.sortField()).toBe(firstColumnToSort.column_key);
    expect(service.sortOrder()).toBe(-1);

    await user.click(firstColumnHeader);
    expect(service.sortField()).toBe(firstColumnToSort.column_key);
    expect(service.sortOrder()).toBe(1);

    const secondColumnToSort = mockComparisonToolColumns[1];
    const secondColumnHeader = screen.getByRole('columnheader', { name: secondColumnToSort.name });

    await user.click(secondColumnHeader);
    expect(service.sortField()).toBe(secondColumnToSort.column_key);
    expect(service.sortOrder()).toBe(-1);
  });
});

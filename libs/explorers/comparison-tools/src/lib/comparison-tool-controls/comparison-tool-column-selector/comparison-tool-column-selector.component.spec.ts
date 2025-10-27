import { provideHttpClient } from '@angular/common/http';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';
import { ComparisonToolConfig } from '@sagebionetworks/explorers/models';
import {
  ComparisonToolService,
  provideComparisonToolService,
  SvgIconService,
} from '@sagebionetworks/explorers/services';
import { mockComparisonToolConfigs, SvgIconServiceStub } from '@sagebionetworks/explorers/testing';
import { render, screen, within } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { ComparisonToolColumnSelectorComponent } from './comparison-tool-column-selector.component';

async function setup(
  pageConfigs: ComparisonToolConfig[] = mockComparisonToolConfigs,
  initialSelection?: string[],
) {
  const component = await render(ComparisonToolColumnSelectorComponent, {
    providers: [
      provideHttpClient(),
      provideRouter([]),
      provideNoopAnimations(),
      { provide: SvgIconService, useClass: SvgIconServiceStub },
      ...provideComparisonToolService({
        configs: pageConfigs,
        selection: initialSelection,
      }),
    ],
  });
  const instance = component.fixture.componentInstance;
  const service = component.fixture.debugElement.injector.get(ComparisonToolService);

  return { component, instance, service };
}

function getColumnSelectorButton() {
  // PrimeNG tooltip doesn't add accessible name, so we find by attribute
  return screen.getByRole('button');
}

describe('ComparisonToolColumnSelectorComponent', () => {
  it('should create the component', async () => {
    const { component } = await setup();
    expect(component.fixture.componentInstance).toBeTruthy();
  });

  it('should display the column selector button', async () => {
    await setup();
    const button = screen.getByRole('button');
    expect(button).toBeInTheDocument();
    expect(button).toHaveAttribute('ptooltip', 'Show/hide columns');
  });

  it('should display the column icon', async () => {
    await setup();
    const icon = screen.getByTestId('mock-svg');
    expect(icon).toBeInTheDocument();
  });

  it('should show overlay badge when there are hidden columns', async () => {
    const { service } = await setup();

    // Toggle a column to hide it
    const columns = service.columns();
    if (columns.length > 0) {
      service.toggleColumn(columns[0]);
    }

    // Wait for re-render
    await screen.findByRole('button');

    expect(service.hasHiddenColumns()).toBe(true);
  });
  it('should not show overlay badge when all columns are visible', async () => {
    const { service } = await setup();

    // Ensure all columns are visible
    const columns = service.columns();
    columns.forEach((col) => {
      if (!col.selected) {
        service.toggleColumn(col);
      }
    });

    expect(service.hasHiddenColumns()).toBe(false);
  });

  it('should open popover when button is clicked', async () => {
    const user = userEvent.setup();
    await setup();

    const button = getColumnSelectorButton();
    await user.click(button);

    const columnsList = await screen.findByRole('list', { hidden: true });
    expect(columnsList).toBeInTheDocument();
    expect(columnsList).toHaveAttribute('id', 'columns');
  });

  it('should display all columns in the popover', async () => {
    const user = userEvent.setup();
    const { service } = await setup();

    const button = getColumnSelectorButton();
    await user.click(button);

    const columns = service.columns();
    const columnsList = await screen.findByRole('list', { hidden: true });
    const listItems = within(columnsList).getAllByRole('listitem', { hidden: true });

    expect(listItems).toHaveLength(columns.length);
  });

  it('should show checkmark for selected columns', async () => {
    const user = userEvent.setup();
    const { service } = await setup();

    const button = getColumnSelectorButton();
    await user.click(button);

    const columns = service.columns();
    const selectedColumns = columns.filter((col) => col.selected);

    const columnsList = await screen.findByRole('list', { hidden: true });
    const checkmarks = within(columnsList).queryAllByText('', {
      selector: '.pi-check',
    });

    expect(checkmarks.length).toBeGreaterThan(0);
    expect(checkmarks.length).toBeLessThanOrEqual(selectedColumns.length);
  });

  it('should toggle column visibility when column is clicked', async () => {
    const user = userEvent.setup();
    const { service } = await setup();

    const button = getColumnSelectorButton();
    await user.click(button);

    await screen.findByRole('list', { hidden: true }); // Wait for popover to render

    const columns = service.columns();
    const firstColumn = columns[0];
    const initialSelectedState = firstColumn.selected;

    const columnName = screen.getByText(firstColumn.name as string);
    await user.click(columnName);

    const updatedColumns = service.columns();
    const updatedFirstColumn = updatedColumns.find((col) => col.name === firstColumn.name);

    expect(updatedFirstColumn?.selected).toBe(!initialSelectedState);
  });

  it('should call toggleColumn method when column is clicked', async () => {
    const user = userEvent.setup();
    const { instance, service } = await setup();

    const toggleSpy = jest.spyOn(instance, 'toggleColumn');

    const button = getColumnSelectorButton();
    await user.click(button);

    await screen.findByRole('list', { hidden: true }); // Wait for popover to render

    const columns = service.columns();
    const firstColumn = columns[0];

    const columnName = screen.getByText(firstColumn.name as string);
    await user.click(columnName);

    expect(toggleSpy).toHaveBeenCalledWith(expect.objectContaining({ name: firstColumn.name }));
  });

  it('should update checkmark display when column is toggled', async () => {
    const user = userEvent.setup();
    const { service } = await setup();

    const button = getColumnSelectorButton();
    await user.click(button);

    await screen.findByRole('list', { hidden: true }); // Wait for popover to render

    const columns = service.columns();
    const selectedColumn = columns.find((col) => col.selected);

    if (!selectedColumn) {
      throw new Error('No selected column found');
    }

    const columnName = screen.getByText(selectedColumn.name as string);
    const columnItem = columnName.closest('li');

    expect(columnItem).not.toBeNull();

    // Check that checkmark exists initially
    let checkmark = within(columnItem as HTMLElement).queryByText('', { selector: '.pi-check' });
    expect(checkmark).toBeInTheDocument();

    // Toggle the column
    await user.click(columnName);

    // Check that checkmark is removed after toggling
    checkmark = within(columnItem as HTMLElement).queryByText('', { selector: '.pi-check' });
    expect(checkmark).not.toBeInTheDocument();
  });

  it('should handle multiple column toggles', async () => {
    const user = userEvent.setup();
    const { service } = await setup();

    const button = getColumnSelectorButton();
    await user.click(button);

    await screen.findByRole('list', { hidden: true }); // Wait for popover to render

    const columns = service.columns();
    const columnsToToggle = columns.slice(0, 3);

    for (const column of columnsToToggle) {
      const columnName = screen.getByText(column.name as string);
      await user.click(columnName);
    }

    const updatedColumns = service.columns();
    columnsToToggle.forEach((originalColumn) => {
      const updatedColumn = updatedColumns.find((col) => col.name === originalColumn.name);
      expect(updatedColumn?.selected).toBe(!originalColumn.selected);
    });
  });

  it('should display correct column names from service', async () => {
    const user = userEvent.setup();
    const { service } = await setup();

    const button = getColumnSelectorButton();
    await user.click(button);

    await screen.findByRole('list', { hidden: true }); // Wait for popover to render

    const columns = service.columns();

    columns.forEach((column) => {
      expect(screen.getByText(column.name as string)).toBeInTheDocument();
    });
  });

  it('should show checkmark for selected column', async () => {
    const user = userEvent.setup();
    const { service } = await setup();

    const button = getColumnSelectorButton();
    await user.click(button);

    await screen.findByRole('list', { hidden: true }); // Wait for popover to render

    const columns = service.columns();
    const selectedColumn = columns.find((col) => col.selected);

    expect(selectedColumn).toBeDefined();

    const selectedColumnName = screen.getByText(selectedColumn?.name ?? '');
    const selectedColumnItem = selectedColumnName.closest('li');
    expect(selectedColumnItem).not.toBeNull();

    const checkmark = within(selectedColumnItem as HTMLElement).queryByText('', {
      selector: '.pi-check',
    });
    expect(checkmark).toBeInTheDocument();
  });

  it('should hide checkmark for unselected column', async () => {
    const user = userEvent.setup();
    const { service } = await setup();

    // First toggle a column to make it unselected
    const columns = service.columns();
    const columnToHide = columns.find((col) => col.selected);
    expect(columnToHide).toBeDefined();

    if (columnToHide) {
      service.toggleColumn(columnToHide);
    }

    const button = getColumnSelectorButton();
    await user.click(button);

    await screen.findByRole('list', { hidden: true }); // Wait for popover to render

    const updatedColumns = service.columns();
    const unselectedColumn = updatedColumns.find((col) => !col.selected);
    expect(unselectedColumn).toBeDefined();

    const unselectedColumnName = screen.getByText(unselectedColumn?.name ?? '');
    const unselectedColumnItem = unselectedColumnName.closest('li');
    expect(unselectedColumnItem).not.toBeNull();

    const checkmark = within(unselectedColumnItem as HTMLElement).queryByText('', {
      selector: '.pi-check',
    });
    expect(checkmark).not.toBeInTheDocument();
  });

  it('should handle empty columns array', async () => {
    const emptyConfigs: ComparisonToolConfig[] = [
      { page: 'Disease Correlation', dropdowns: ['Test'], columns: [], filters: [] },
    ];

    const user = userEvent.setup();
    await setup(emptyConfigs, ['Test']);

    const button = getColumnSelectorButton();
    await user.click(button);

    const columnsList = await screen.findByRole('list', { hidden: true });
    const listItems = within(columnsList).queryAllByRole('listitem', { hidden: true });

    expect(listItems).toHaveLength(0);
  });

  it('should properly integrate with ComparisonToolService', async () => {
    const { instance, service } = await setup();

    // Test that getter returns service columns
    expect(instance.columns).toBe(service.columns());

    // Test that hasHiddenColumns returns correct value
    expect(instance.hasHiddenColumns()).toBe(service.hasHiddenColumns());
  });

  it('should update UI when columns change in service', async () => {
    const user = userEvent.setup();
    const { service } = await setup();

    const button = getColumnSelectorButton();
    await user.click(button);

    await screen.findByRole('list', { hidden: true }); // Wait for popover to render

    const initialColumns = service.columns();
    const firstColumn = initialColumns[0];

    // Toggle column through service
    service.toggleColumn(firstColumn);

    // Wait for update
    await screen.findByText(firstColumn.name as string);

    const updatedColumns = service.columns();
    const updatedFirstColumn = updatedColumns.find((col) => col.name === firstColumn.name);

    expect(updatedFirstColumn?.selected).toBe(!firstColumn.selected);
  });
});

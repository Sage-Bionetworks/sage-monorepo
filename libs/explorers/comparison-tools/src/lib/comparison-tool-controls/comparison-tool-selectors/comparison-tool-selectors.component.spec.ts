import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { ComparisonToolConfig } from '@sagebionetworks/explorers/models';
import { mockComparisonToolConfigs } from '@sagebionetworks/explorers/testing';
import { render, screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { ComparisonToolSelectorsComponent } from './comparison-tool-selectors.component';

async function setup(
  pageConfigs: ComparisonToolConfig[] = mockComparisonToolConfigs,
  initialSelection?: string[],
) {
  const user = userEvent.setup();
  const component = await render(ComparisonToolSelectorsComponent, {
    providers: [provideHttpClient(), provideRouter([])],
    componentInputs: {
      pageConfigs,
      dropdownsSelection: initialSelection || [],
    },
  });
  const instance = component.fixture.componentInstance;

  return { user, component, instance };
}

function getAllDropdowns() {
  return screen.getAllByRole('combobox');
}

describe('ComparisonToolSelectorsComponent', () => {
  it('should create the component', async () => {
    const { component } = await setup();
    expect(component.fixture.componentInstance).toBeTruthy();
  });

  it('should display no dropdowns when pageConfigs is empty', async () => {
    await setup([]);
    expect(screen.queryByRole('combobox')).not.toBeInTheDocument();
  });

  it('should display no dropdowns when all configs have empty dropdowns arrays', async () => {
    const emptyConfigs: ComparisonToolConfig[] = [
      { page: 'Disease Correlation', dropdowns: [], columns: [], filters: [] },
    ];
    await setup(emptyConfigs);
    expect(screen.queryByRole('combobox')).not.toBeInTheDocument();
  });

  it('should display correct initial dropdowns with provided selection', async () => {
    const { instance } = await setup(mockComparisonToolConfigs, ['Red', 'Crimson']);
    await screen.findByRole('combobox', { name: 'Red' });
    await screen.findByRole('combobox', { name: 'Crimson' });
    expect(getAllDropdowns()).toHaveLength(2);
    expect(instance.selection()).toEqual(['Red', 'Crimson']);
  });

  it('should use provided initial selection when valid', async () => {
    const selectedDropdowns = ['Blue', 'Light Blue', 'Powder Blue'];
    const { instance } = await setup(mockComparisonToolConfigs, selectedDropdowns);
    expect(instance.selection()).toEqual(selectedDropdowns);
    const dropdowns = getAllDropdowns();
    expect(dropdowns).toHaveLength(selectedDropdowns.length);
    for (let i = 0; i < dropdowns.length; i++) {
      await screen.findByRole('combobox', { name: selectedDropdowns[i] });
    }
  });

  it('should fall back to default selection when initial selection is invalid', async () => {
    const { instance } = await setup(mockComparisonToolConfigs, ['NonExistent', 'Option']);
    expect(instance.selection()).toEqual(['Red', 'Crimson']);
  });

  it('should auto-select subsequent levels when changing dropdown selection', async () => {
    const { instance } = await setup();
    instance.onDropdownChange(0, 'Blue');
    expect(instance.selection()).toEqual(['Blue', 'Light Blue', 'Powder Blue']);
  });

  it('should handle single level dropdown configurations', async () => {
    const singleLevelConfigs: ComparisonToolConfig[] = [
      { page: 'Disease Correlation', dropdowns: ['Option1'], columns: [], filters: [] },
      { page: 'Disease Correlation', dropdowns: ['Option2'], columns: [], filters: [] },
    ];

    const { instance } = await setup(singleLevelConfigs, ['Option1']);

    expect(getAllDropdowns()).toHaveLength(1);
    expect(instance.selection()).toEqual(['Option1']);
  });

  it('should handle deep nested dropdown configurations', async () => {
    const deepConfigs: ComparisonToolConfig[] = [
      {
        page: 'Disease Correlation',
        dropdowns: ['Level1', 'Level2', 'Level3', 'Level4', 'Level5'],
        columns: [],
        filters: [],
      },
    ];

    const initialSelection = ['Level1', 'Level2', 'Level3', 'Level4', 'Level5'];
    const { instance } = await setup(deepConfigs, initialSelection);

    expect(instance.selection()).toEqual(initialSelection);
    expect(screen.queryAllByRole('combobox')).toHaveLength(0);
    for (const level of initialSelection) {
      expect(screen.getByText(level)).toBeInTheDocument();
    }
  });

  it('should return correct selected value for each level', async () => {
    const { instance } = await setup(mockComparisonToolConfigs, ['Red', 'Crimson']);

    expect(instance.getSelectedValueForLevel(0)).toBe('Red');
    expect(instance.getSelectedValueForLevel(1)).toBe('Crimson');
    expect(instance.getSelectedValueForLevel(2)).toBeUndefined();
  });

  it('should handle selection change to first level dropdown', async () => {
    const { instance } = await setup(mockComparisonToolConfigs, ['Red', 'Crimson']);

    instance.onDropdownChange(0, 'Blue');

    expect(instance.selection()).toEqual(['Blue', 'Light Blue', 'Powder Blue']);
    expect(instance.getSelectedValueForLevel(0)).toBe('Blue');
    expect(instance.getSelectedValueForLevel(1)).toBe('Light Blue');
    expect(instance.getSelectedValueForLevel(2)).toBe('Powder Blue');
  });

  it('should handle selection change to non-first level dropdown', async () => {
    const { instance } = await setup(mockComparisonToolConfigs, ['Red', 'Crimson']);

    instance.onDropdownChange(0, 'Blue');
    expect(instance.getSelectedValueForLevel(2)).toBe('Powder Blue');

    instance.onDropdownChange(1, 'Dark Blue');
    expect(instance.selection()).toEqual(['Blue', 'Dark Blue', 'Navy Blue']);

    expect(instance.getSelectedValueForLevel(0)).toBe('Blue');
    expect(instance.getSelectedValueForLevel(1)).toBe('Dark Blue');
    expect(instance.getSelectedValueForLevel(2)).toBe('Navy Blue');
  });

  it('should not set selection when no configs provided', async () => {
    const emptyConfigs: ComparisonToolConfig[] = [];
    const { instance } = await setup(emptyConfigs);
    expect(instance.selection()).toEqual([]);
  });
});

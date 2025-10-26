import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { ComparisonToolConfig } from '@sagebionetworks/explorers/models';
import {
  ComparisonToolService,
  provideComparisonToolService,
} from '@sagebionetworks/explorers/services';
import { mockComparisonToolConfigs } from '@sagebionetworks/explorers/testing';
import { render, screen } from '@testing-library/angular';
import { ComparisonToolCategorySelectorsComponent } from './comparison-tool-category-selectors.component';

async function setup(
  pageConfigs: ComparisonToolConfig[] = mockComparisonToolConfigs,
  initialSelection?: string[],
) {
  const component = await render(ComparisonToolCategorySelectorsComponent, {
    providers: [
      provideHttpClient(),
      provideRouter([]),
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
    const { service } = await setup(mockComparisonToolConfigs, ['Red', 'Crimson']);
    await screen.findByRole('combobox', { name: 'Red' });
    await screen.findByRole('combobox', { name: 'Crimson' });
    expect(getAllDropdowns()).toHaveLength(2);
    expect(service.dropdownSelection()).toEqual(['Red', 'Crimson']);
  });

  it('should use provided initial selection when valid', async () => {
    const selectedDropdowns = ['Blue', 'Light Blue', 'Powder Blue'];
    const { service } = await setup(mockComparisonToolConfigs, selectedDropdowns);
    expect(service.dropdownSelection()).toEqual(selectedDropdowns);
    const dropdowns = getAllDropdowns();
    expect(dropdowns).toHaveLength(selectedDropdowns.length);
    for (let i = 0; i < dropdowns.length; i++) {
      await screen.findByRole('combobox', { name: selectedDropdowns[i] });
    }
  });

  it('should fall back to default selection when initial selection is invalid', async () => {
    const { service } = await setup(mockComparisonToolConfigs, ['NonExistent', 'Option']);
    expect(service.dropdownSelection()).toEqual(['Red', 'Crimson']);
  });

  it('should auto-select subsequent levels when changing dropdown selection', async () => {
    const { instance, service } = await setup();
    instance.onDropdownChange(0, 'Blue');
    expect(service.dropdownSelection()).toEqual(['Blue', 'Light Blue', 'Powder Blue']);
  });

  it('should handle single level dropdown configurations', async () => {
    const singleLevelConfigs: ComparisonToolConfig[] = [
      { page: 'Disease Correlation', dropdowns: ['Option1'], columns: [], filters: [] },
      { page: 'Disease Correlation', dropdowns: ['Option2'], columns: [], filters: [] },
    ];

    const { service } = await setup(singleLevelConfigs, ['Option1']);

    expect(getAllDropdowns()).toHaveLength(1);
    expect(service.dropdownSelection()).toEqual(['Option1']);
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
    const { service } = await setup(deepConfigs, initialSelection);

    expect(service.dropdownSelection()).toEqual(initialSelection);
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
    const { instance, service } = await setup(mockComparisonToolConfigs, ['Red', 'Crimson']);

    instance.onDropdownChange(0, 'Blue');

    expect(service.dropdownSelection()).toEqual(['Blue', 'Light Blue', 'Powder Blue']);
    expect(instance.getSelectedValueForLevel(0)).toBe('Blue');
    expect(instance.getSelectedValueForLevel(1)).toBe('Light Blue');
    expect(instance.getSelectedValueForLevel(2)).toBe('Powder Blue');
  });

  it('should handle selection change to non-first level dropdown', async () => {
    const { instance, service } = await setup(mockComparisonToolConfigs, ['Red', 'Crimson']);

    instance.onDropdownChange(0, 'Blue');
    expect(instance.getSelectedValueForLevel(2)).toBe('Powder Blue');

    instance.onDropdownChange(1, 'Dark Blue');
    expect(service.dropdownSelection()).toEqual(['Blue', 'Dark Blue', 'Navy Blue']);

    expect(instance.getSelectedValueForLevel(0)).toBe('Blue');
    expect(instance.getSelectedValueForLevel(1)).toBe('Dark Blue');
    expect(instance.getSelectedValueForLevel(2)).toBe('Navy Blue');
  });

  it('should not set selection when no configs provided', async () => {
    const emptyConfigs: ComparisonToolConfig[] = [];
    const { service } = await setup(emptyConfigs);
    expect(service.dropdownSelection()).toEqual([]);
  });
});

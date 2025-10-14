import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { ComparisonToolConfig } from '@sagebionetworks/explorers/models';
import { mockComparisonToolConfigs } from '@sagebionetworks/explorers/testing';
import { render, screen, waitFor } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { ComparisonToolSelectorsComponent } from './comparison-tool-selectors.component';

const mockSelectionChanged = jest.fn();

async function setup(
  pageConfigs: ComparisonToolConfig[] = mockComparisonToolConfigs,
  initialSelection?: string[],
) {
  const user = userEvent.setup();
  const component = await render(ComparisonToolSelectorsComponent, {
    providers: [provideHttpClient(), provideRouter([])],
    componentInputs: {
      pageConfigs,
      initialSelection: initialSelection || [],
    },
    on: {
      selectionChanged: mockSelectionChanged,
    },
  });
  const instance = component.fixture.componentInstance;

  return { user, component, instance };
}

function getAllDropdowns() {
  return screen.getAllByRole('combobox');
}

describe('ComparisonToolSelectorsComponent', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

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

  it('should display correct initial dropdowns with default selection', async () => {
    await setup();
    await screen.findByRole('combobox', { name: 'Red' });
    await screen.findByRole('combobox', { name: 'Crimson' });
    expect(getAllDropdowns()).toHaveLength(2);
  });

  it('should emit initial selection on component initialization', async () => {
    await setup();
    await waitFor(() => {
      expect(mockSelectionChanged).toHaveBeenCalledWith(['Red', 'Crimson']);
    });
  });

  it('should use provided initial selection when valid', async () => {
    const selectedDropdowns = ['Blue', 'Light Blue', 'Powder Blue'];
    await setup(mockComparisonToolConfigs, selectedDropdowns);
    await waitFor(() => {
      expect(mockSelectionChanged).toHaveBeenCalledWith(selectedDropdowns);
    });
    const dropdowns = getAllDropdowns();
    expect(dropdowns).toHaveLength(selectedDropdowns.length);
    for (let i = 0; i < dropdowns.length; i++) {
      await screen.findByRole('combobox', { name: selectedDropdowns[i] });
    }
  });

  it('should fall back to default selection when initial selection is invalid', async () => {
    await setup(mockComparisonToolConfigs, ['NonExistent', 'Option']);
    await waitFor(() => {
      expect(mockSelectionChanged).toHaveBeenCalledWith(['Red', 'Crimson']);
    });
  });

  it('should auto-select subsequent levels when changing dropdown selection', async () => {
    const { component } = await setup();
    component.fixture.componentInstance.onDropdownChange(0, 'Blue');
    await waitFor(() => {
      expect(mockSelectionChanged).toHaveBeenCalledWith(['Blue', 'Light Blue', 'Powder Blue']);
    });
  });

  it('should handle single level dropdown configurations', async () => {
    const singleLevelConfigs: ComparisonToolConfig[] = [
      { page: 'Disease Correlation', dropdowns: ['Option1'], columns: [], filters: [] },
      { page: 'Disease Correlation', dropdowns: ['Option2'], columns: [], filters: [] },
    ];

    await setup(singleLevelConfigs);

    expect(getAllDropdowns()).toHaveLength(1);
    await waitFor(() => {
      expect(mockSelectionChanged).toHaveBeenCalledWith(['Option1']);
    });
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

    await setup(deepConfigs);

    expect(getAllDropdowns()).toHaveLength(5);
    await waitFor(() => {
      expect(mockSelectionChanged).toHaveBeenCalledWith([
        'Level1',
        'Level2',
        'Level3',
        'Level4',
        'Level5',
      ]);
    });
  });

  it('should return correct selected value for each level', async () => {
    const { instance } = await setup();

    await waitFor(() => {
      expect(mockSelectionChanged).toHaveBeenCalled();
    });

    expect(instance.getSelectedValueForLevel(0)).toBe('Red');
    expect(instance.getSelectedValueForLevel(1)).toBe('Crimson');
    expect(instance.getSelectedValueForLevel(2)).toBeUndefined();
  });

  it('should handle selection change to first level dropdown', async () => {
    const { instance } = await setup();

    instance.onDropdownChange(0, 'Blue');

    await waitFor(() => {
      expect(mockSelectionChanged).toHaveBeenCalledWith(['Blue', 'Light Blue', 'Powder Blue']);
    });

    expect(instance.getSelectedValueForLevel(0)).toBe('Blue');
    expect(instance.getSelectedValueForLevel(1)).toBe('Light Blue');
    expect(instance.getSelectedValueForLevel(2)).toBe('Powder Blue');
  });

  it('should handle selection change to non-first level dropdown', async () => {
    const { instance } = await setup();

    instance.onDropdownChange(0, 'Blue');

    await waitFor(() => {
      expect(instance.getSelectedValueForLevel(2)).toBe('Powder Blue');
    });

    instance.onDropdownChange(1, 'Dark Blue');

    await waitFor(() => {
      expect(mockSelectionChanged).toHaveBeenLastCalledWith(['Blue', 'Dark Blue', 'Navy Blue']);
    });

    expect(instance.getSelectedValueForLevel(0)).toBe('Blue');
    expect(instance.getSelectedValueForLevel(1)).toBe('Dark Blue');
    expect(instance.getSelectedValueForLevel(2)).toBe('Navy Blue');
  });

  it('should not emit selection when no configs provided', async () => {
    const emptyConfigs: ComparisonToolConfig[] = [];
    await setup(emptyConfigs);
    expect(mockSelectionChanged).not.toHaveBeenCalled();
  });
});

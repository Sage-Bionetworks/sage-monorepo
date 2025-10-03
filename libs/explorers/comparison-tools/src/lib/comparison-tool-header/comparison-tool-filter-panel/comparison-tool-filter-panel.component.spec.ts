import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { HelperService, SvgIconService } from '@sagebionetworks/explorers/services';
import {
  mockComparisonToolFilterConfigs,
  mockComparisonToolFilters,
  SvgIconServiceStub,
} from '@sagebionetworks/explorers/testing';
import { render, screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { ComparisonToolFilterPanelComponent } from './comparison-tool-filter-panel.component';

async function setup(isOpen = false) {
  const user = userEvent.setup();
  const component = await render(ComparisonToolFilterPanelComponent, {
    providers: [
      provideRouter([]),
      HelperService,
      provideHttpClient(),
      { provide: SvgIconService, useClass: SvgIconServiceStub },
    ],
    componentInputs: {
      filterConfigs: mockComparisonToolFilterConfigs,
      isOpen,
    },
  });
  const componentInstance = component.fixture.componentInstance;
  return { user, component, componentInstance };
}

function getCategoryButton(category: string) {
  return screen.getByRole('button', { name: `open ${category} filter options` });
}

function getOptionCheckbox(option: string) {
  return screen.getByRole('checkbox', { name: option });
}

describe('ComparisonToolFilterPanelComponent', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should create', async () => {
    const { componentInstance } = await setup();
    expect(componentInstance).toBeTruthy();
  });

  it('should display filter heading', async () => {
    await setup(true);
    expect(screen.getByText('Filter By')).toBeInTheDocument();
  });

  it('should display all filter categories', async () => {
    await setup(true);
    mockComparisonToolFilterConfigs.forEach((config) => {
      expect(getCategoryButton(config.name)).toBeInTheDocument();
    });
  });

  it('should show filter panel when open', async () => {
    await setup(true);
    const panel = document.querySelector('.filter-panel');
    expect(panel).toHaveClass('open');
  });

  it('should hide filter panel when closed', async () => {
    await setup(false);
    const panel = document.querySelector('.filter-panel');
    expect(panel).not.toHaveClass('open');
  });

  it('should close panel when close button is clicked', async () => {
    const { user, componentInstance } = await setup(true);
    const closeButton = screen.getByRole('button', { name: 'close' });
    await user.click(closeButton);
    expect(componentInstance.isOpen()).toBe(false);
  });

  it('should open filter pane when category button is clicked', async () => {
    const { user, componentInstance } = await setup(true);
    const categoryButton = getCategoryButton(mockComparisonToolFilterConfigs[0].name);
    await user.click(categoryButton);
    expect(componentInstance.activePane).toBe(0);
  });

  it('should display filter options when pane is opened', async () => {
    const { user } = await setup(true);

    const categoryButton = getCategoryButton(mockComparisonToolFilterConfigs[0].name);
    await user.click(categoryButton);

    mockComparisonToolFilterConfigs[0].values.forEach((value) => {
      expect(screen.getByLabelText(value)).toBeInTheDocument();
    });
  });

  it('should toggle checkbox when option is clicked', async () => {
    const { user } = await setup(true);

    const categoryButton = getCategoryButton(mockComparisonToolFilterConfigs[0].name);
    await user.click(categoryButton);

    const checkbox = getOptionCheckbox(mockComparisonToolFilterConfigs[0].values[0]);
    expect(checkbox).not.toBeChecked();

    await user.click(checkbox);

    expect(checkbox).toBeChecked();
  });

  it('should emit filtersChanged when option is selected', async () => {
    const { user, componentInstance } = await setup(true);

    const filtersChangedSpy = jest.fn();
    componentInstance.filtersChanged.subscribe(filtersChangedSpy);

    const categoryButton = getCategoryButton(mockComparisonToolFilterConfigs[0].name);
    await user.click(categoryButton);

    const checkbox = getOptionCheckbox(mockComparisonToolFilterConfigs[0].values[0]);
    await user.click(checkbox);

    const expectedFilters = JSON.parse(JSON.stringify(mockComparisonToolFilters));
    expectedFilters[0].options[0].selected = true;

    expect(filtersChangedSpy).toHaveBeenCalledWith(expectedFilters);
  });

  it('should show active state when pane is open', async () => {
    const { user } = await setup(true);

    const categoryButton = getCategoryButton(mockComparisonToolFilterConfigs[0].name);
    await user.click(categoryButton);

    const panel = document.querySelector('.filter-panel');
    expect(panel).toHaveClass('active');
  });
});

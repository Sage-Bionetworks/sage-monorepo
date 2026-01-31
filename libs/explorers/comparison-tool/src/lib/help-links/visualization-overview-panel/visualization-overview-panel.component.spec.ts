import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { VisualizationOverviewPane } from '@sagebionetworks/explorers/models';
import {
  AppCookieService,
  ComparisonToolService,
  provideComparisonToolService,
  provideExplorersConfig,
} from '@sagebionetworks/explorers/services';
import {
  mockComparisonToolDataConfig,
  mockVisualizationOverviewPanes,
} from '@sagebionetworks/explorers/testing';
import { render, screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { VisualizationOverviewPanelComponent } from './visualization-overview-panel.component';

describe('VisualizationOverviewPanelComponent', () => {
  const createMockCookieService = (isHidden = false) => ({
    isVisualizationOverviewHidden: jest.fn().mockReturnValue(isHidden),
    setVisualizationOverviewHidden: jest.fn(),
  });

  async function setup(options?: {
    isVisible?: boolean;
    isHidden?: boolean;
    visualizationOverviewPanes?: VisualizationOverviewPane[];
  }) {
    const user = userEvent.setup();
    const mockCookieService = createMockCookieService(options?.isHidden);
    const panes = options?.visualizationOverviewPanes ?? mockVisualizationOverviewPanes;

    const { fixture } = await render(VisualizationOverviewPanelComponent, {
      providers: [
        provideNoopAnimations(),
        { provide: AppCookieService, useValue: mockCookieService },
        provideExplorersConfig({ visualizationOverviewPanes: panes }),
        ...provideComparisonToolService({
          configs: mockComparisonToolDataConfig,
        }),
      ],
    });

    const component = fixture.componentInstance;
    const comparisonToolService = fixture.debugElement.injector.get(ComparisonToolService);

    // Set initial visibility states if provided
    if (options?.isVisible !== undefined) {
      comparisonToolService.setVisualizationOverviewVisibility(options.isVisible);
      fixture.detectChanges();
    }

    // Spy on service methods
    const setVisualizationOverviewVisibilitySpy = jest.spyOn(
      comparisonToolService,
      'setVisualizationOverviewVisibility',
    );

    return {
      component,
      fixture,
      user,
      comparisonToolService,
      mockCookieService,
      setVisualizationOverviewVisibilitySpy,
    };
  }

  it('should create the component', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });

  it('should initialize with default panes', async () => {
    const { component } = await setup();
    expect(component.panes).toHaveLength(4);
    expect(component.panes[0].heading).toBe('Comparison Tool Tutorial');
  });

  it('should return empty array when empty panes are configured', async () => {
    const { component } = await setup({ visualizationOverviewPanes: [] });
    expect(component.panes).toHaveLength(0);
  });

  it('should use panes from provideExplorersConfig', async () => {
    const appConfigPanes: VisualizationOverviewPane[] = [
      { heading: 'App Config Pane', content: '<p>From app config</p>' },
    ];
    const { component } = await setup({ visualizationOverviewPanes: appConfigPanes });
    expect(component.panes).toHaveLength(1);
    expect(component.panes[0].heading).toBe('App Config Pane');
  });

  it('should display dialog when isVisualizationOverviewVisible is true', async () => {
    await setup({ isVisible: true });
    expect(screen.getByRole('dialog')).toBeInTheDocument();
  });

  it('should not display dialog when isVisualizationOverviewVisible is false', async () => {
    await setup({ isVisible: false });
    expect(screen.queryByRole('dialog')).not.toBeInTheDocument();
  });

  it('should display the first pane heading by default', async () => {
    await setup();
    expect(screen.getByText('Comparison Tool Tutorial')).toBeInTheDocument();
  });

  it('should display the first pane content by default', async () => {
    await setup();
    expect(screen.getByText(/Comparison Tools allow you to discover/)).toBeInTheDocument();
  });

  it('should start with activePane set to 0', async () => {
    const { component } = await setup();
    expect(component.activePane).toBe(0);
  });

  describe('Navigation', () => {
    it('should show Next button on first pane', async () => {
      await setup();
      expect(screen.getByRole('button', { name: /next/i })).toBeInTheDocument();
    });

    it('should not show Back button on first pane', async () => {
      await setup();
      expect(screen.queryByRole('button', { name: /back/i })).not.toBeInTheDocument();
    });

    it('should increment activePane when Next button is clicked', async () => {
      const { component, user } = await setup();
      const nextButton = screen.getByRole('button', { name: /next/i });

      await user.click(nextButton);

      expect(component.activePane).toBe(1);
    });

    it('should display second pane content after clicking Next', async () => {
      const { user, fixture } = await setup();
      const nextButton = screen.getByRole('button', { name: /next/i });

      await user.click(nextButton);
      fixture.detectChanges();

      expect(screen.getByText('Search & Filter')).toBeInTheDocument();
      expect(
        screen.getByText(/Enter a value to get a list of matching results/),
      ).toBeInTheDocument();
    });

    it('should show Back button on second pane', async () => {
      const { user } = await setup();
      const nextButton = screen.getByRole('button', { name: /next/i });

      await user.click(nextButton);

      expect(screen.getByRole('button', { name: /back/i })).toBeInTheDocument();
    });

    it('should decrement activePane when Back button is clicked', async () => {
      const { component, user } = await setup();

      // Navigate to second pane
      await user.click(screen.getByRole('button', { name: /next/i }));
      expect(component.activePane).toBe(1);

      // Navigate back
      await user.click(screen.getByRole('button', { name: /back/i }));
      expect(component.activePane).toBe(0);
    });

    it('should not decrement activePane below 0', async () => {
      const { component } = await setup();

      component.previous();

      expect(component.activePane).toBe(0);
    });

    it('should show Close button on last pane', async () => {
      const { user } = await setup();

      // Navigate to last pane (4 panes, need 3 clicks to get to index 3)
      await user.click(screen.getByRole('button', { name: /next/i }));
      await user.click(screen.getByRole('button', { name: /next/i }));
      await user.click(screen.getByRole('button', { name: /next/i }));

      expect(screen.getByRole('button', { name: /close/i })).toBeInTheDocument();
      expect(screen.queryByRole('button', { name: /next/i })).not.toBeInTheDocument();
    });

    it('should not increment activePane beyond last pane', async () => {
      const { component } = await setup();
      component.activePane = 3; // Last pane (4 panes, index 3)

      component.next();

      expect(component.activePane).toBe(3);
    });
  });

  describe('Checkbox "Don\'t show this again"', () => {
    it('should render checkbox with label', async () => {
      await setup();
      expect(screen.getByLabelText(/not to show this again/i)).toBeInTheDocument();
    });

    it('should initialize checkbox as unchecked when cookie is empty', async () => {
      await setup({ isHidden: false });
      const checkbox = screen.getByLabelText(/not to show this again/i) as HTMLInputElement;
      expect(checkbox.checked).toBe(false);
    });

    it('should initialize checkbox as checked when cookie is "1"', async () => {
      const { fixture } = await setup({ isHidden: true, isVisible: true });
      fixture.detectChanges();
      const checkbox = screen.getByLabelText(/not to show this again/i) as HTMLInputElement;
      expect(checkbox.checked).toBe(true);
    });

    it('should save cookie immediately when checkbox is clicked', async () => {
      const { user, mockCookieService } = await setup({ isHidden: false });
      const checkbox = screen.getByLabelText(/not to show this again/i);

      await user.click(checkbox);

      expect(mockCookieService.setVisualizationOverviewHidden).toHaveBeenCalledWith(true);
    });

    it('should delete cookie when unchecking checkbox', async () => {
      const { user, mockCookieService } = await setup({ isHidden: true, isVisible: true });
      const checkbox = screen.getByLabelText(/not to show this again/i);

      await user.click(checkbox);

      expect(mockCookieService.setVisualizationOverviewHidden).toHaveBeenCalledWith(false);
    });
  });

  describe('Dialog Close', () => {
    it('should call setVisualizationOverviewVisibility with false when Close button is clicked', async () => {
      const { user, setVisualizationOverviewVisibilitySpy } = await setup();

      // Navigate to last pane where Close button is shown (4 panes, need 3 clicks)
      await user.click(screen.getByRole('button', { name: /next/i }));
      await user.click(screen.getByRole('button', { name: /next/i }));
      await user.click(screen.getByRole('button', { name: /next/i }));

      const closeButton = screen.getByRole('button', { name: /close/i });
      await user.click(closeButton);

      expect(setVisualizationOverviewVisibilitySpy).toHaveBeenCalledWith(false);
    });

    it('should reset activePane to 0 when dialog is hidden', async () => {
      const { component } = await setup();

      // Navigate to second pane
      component.activePane = 1;

      // Simulate dialog hide
      component.onHide();

      expect(component.activePane).toBe(0);
    });
  });

  describe('Cookie Initialization', () => {
    it('should hide dialog when cookie value is "1"', async () => {
      const { component, comparisonToolService } = await setup({
        isHidden: true,
      });

      expect(component.willHide).toBe(true);
      expect(comparisonToolService.isVisualizationOverviewVisible()).toBe(false);
    });

    it('should show dialog when cookie value is empty', async () => {
      const { component, comparisonToolService, mockCookieService } = await setup({
        isHidden: false,
      });

      expect(mockCookieService.isVisualizationOverviewHidden).toHaveBeenCalled();
      expect(component.willHide).toBe(false);
      expect(comparisonToolService.isVisualizationOverviewVisible()).toBe(true);
    });

    it('should show dialog when cookie value is "0"', async () => {
      const { component, comparisonToolService } = await setup({
        isHidden: false,
      });

      expect(component.willHide).toBe(false);
      expect(comparisonToolService.isVisualizationOverviewVisible()).toBe(true);
    });
  });
});

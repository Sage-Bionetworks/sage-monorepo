import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { VisualizationOverviewPane } from '@sagebionetworks/explorers/models';
import {
  provideComparisonToolService,
  ComparisonToolService,
} from '@sagebionetworks/explorers/services';
import { mockComparisonToolDataConfig } from '@sagebionetworks/explorers/testing';
import { render, screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { VisualizationOverviewPanelComponent } from './visualization-overview-panel.component';

describe('VisualizationOverviewPanelComponent', () => {
  const mockPanes: VisualizationOverviewPane[] = [
    {
      heading: 'Welcome to Visualization',
      content: '<p>First pane content</p>',
    },
    {
      heading: 'How to Use Filters',
      content: '<p>Second pane content</p>',
    },
    {
      heading: 'Export Your Data',
      content: '<p>Third pane content</p>',
    },
  ];

  async function setup(options?: {
    isVisible?: boolean;
    isHiddenByUser?: boolean;
    panes?: VisualizationOverviewPane[];
  }) {
    const user = userEvent.setup();

    const { fixture } = await render(VisualizationOverviewPanelComponent, {
      providers: [
        provideNoopAnimations(),
        ...provideComparisonToolService({
          configs: mockComparisonToolDataConfig,
          viewConfig: {
            visualizationOverviewPanes: options?.panes ?? mockPanes,
          },
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

    if (options?.isHiddenByUser !== undefined) {
      comparisonToolService.setVisualizationOverviewHiddenByUser(options.isHiddenByUser);
      fixture.detectChanges();
    }

    // Spy on service methods
    const setVisualizationOverviewVisibilitySpy = jest.spyOn(
      comparisonToolService,
      'setVisualizationOverviewVisibility',
    );
    const setVisualizationOverviewHiddenByUserSpy = jest.spyOn(
      comparisonToolService,
      'setVisualizationOverviewHiddenByUser',
    );

    return {
      component,
      fixture,
      user,
      comparisonToolService,
      setVisualizationOverviewVisibilitySpy,
      setVisualizationOverviewHiddenByUserSpy,
    };
  }

  it('should create the component', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });

  it('should initialize with correct panes from viewConfig', async () => {
    const { component } = await setup();
    const panes = component.panes();
    expect(panes).toHaveLength(3);
    expect(panes[0].heading).toBe('Welcome to Visualization');
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
    expect(screen.getByText('Welcome to Visualization')).toBeInTheDocument();
  });

  it('should display the first pane content by default', async () => {
    await setup();
    expect(screen.getByText('First pane content')).toBeInTheDocument();
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

      expect(screen.getByText('How to Use Filters')).toBeInTheDocument();
      expect(screen.getByText('Second pane content')).toBeInTheDocument();
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

      // Navigate to last pane
      await user.click(screen.getByRole('button', { name: /next/i }));
      await user.click(screen.getByRole('button', { name: /next/i }));

      expect(screen.getByRole('button', { name: /close/i })).toBeInTheDocument();
      expect(screen.queryByRole('button', { name: /next/i })).not.toBeInTheDocument();
    });

    it('should not increment activePane beyond last pane', async () => {
      const { component } = await setup();
      component.activePane = 2; // Last pane

      component.next();

      expect(component.activePane).toBe(2);
    });
  });

  describe('Checkbox "Don\'t show this again"', () => {
    it('should render checkbox with label', async () => {
      await setup();
      expect(screen.getByLabelText(/don't show this again/i)).toBeInTheDocument();
    });

    it('should initialize checkbox as unchecked when isHiddenByUser is false', async () => {
      await setup({ isHiddenByUser: false });
      const checkbox = screen.getByLabelText(/don't show this again/i) as HTMLInputElement;
      expect(checkbox.checked).toBe(false);
    });

    it('should initialize checkbox as checked when isHiddenByUser is true', async () => {
      const { fixture } = await setup({ isHiddenByUser: true });
      fixture.detectChanges();
      const checkbox = screen.getByLabelText(/don't show this again/i) as HTMLInputElement;
      expect(checkbox.checked).toBe(true);
    });

    it('should call setVisualizationOverviewHiddenByUser when checkbox is clicked', async () => {
      const { user, setVisualizationOverviewHiddenByUserSpy } = await setup({
        isHiddenByUser: false,
      });
      const checkbox = screen.getByLabelText(/don't show this again/i);

      await user.click(checkbox);

      expect(setVisualizationOverviewHiddenByUserSpy).toHaveBeenCalledWith(true);
    });

    it('should call setVisualizationOverviewHiddenByUser with false when unchecking', async () => {
      const { user, setVisualizationOverviewHiddenByUserSpy } = await setup({
        isHiddenByUser: true,
      });
      const checkbox = screen.getByLabelText(/don't show this again/i);

      await user.click(checkbox);

      expect(setVisualizationOverviewHiddenByUserSpy).toHaveBeenCalledWith(false);
    });
  });

  describe('Dialog Close', () => {
    it('should call setVisualizationOverviewVisibility with false when Close button is clicked', async () => {
      const { user, setVisualizationOverviewVisibilitySpy } = await setup();

      // Navigate to last pane where Close button is shown
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

  describe('Edge Cases', () => {
    it('should show Close button immediately when there is only one pane', async () => {
      const singlePane: VisualizationOverviewPane[] = [
        {
          heading: 'Only Pane',
          content: '<p>Single pane content</p>',
        },
      ];

      await setup({ panes: singlePane });

      expect(screen.getByRole('button', { name: /close/i })).toBeInTheDocument();
      expect(screen.queryByRole('button', { name: /next/i })).not.toBeInTheDocument();
    });
  });
});

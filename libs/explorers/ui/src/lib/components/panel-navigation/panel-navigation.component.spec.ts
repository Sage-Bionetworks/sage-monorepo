import { Panel } from '@sagebionetworks/explorers/models';
import { HelperService } from '@sagebionetworks/explorers/services';
import { render, screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { PanelNavigationComponent } from './panel-navigation.component';

class MockHelperService {
  getOffset = jest.fn().mockReturnValue({ top: 100 });
}

const panelChangeSpy = jest.fn();

const panelsMock: Panel[] = [
  { name: 'panel1', label: 'Panel1', disabled: false },
  { name: 'panel2', label: 'Panel2', disabled: false },
  { name: 'panel3', label: 'Panel3', disabled: true },
];

async function setup(panels: Panel[] = panelsMock, activePanel = 'panel1', activeParent = '') {
  const user = userEvent.setup();
  const component = await render(PanelNavigationComponent, {
    providers: [{ provide: HelperService, useClass: MockHelperService }],
    componentInputs: {
      panels,
      activePanel,
      activeParent,
    },
    componentOutputs: {
      panelChange: { emit: panelChangeSpy } as any,
    },
  });
  return { component, user };
}

describe('PanelNavigationComponent', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });
  afterAll(() => {
    jest.restoreAllMocks();
  });
  it('should create', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });

  it('should emit panelChange when clicking on a panel', async () => {
    const { user } = await setup();
    await user.click(screen.getByRole('button', { name: /panel2/i }));
    expect(panelChangeSpy).toHaveBeenCalledWith(panelsMock[1]);
  });

  it('should hide disabled panels', async () => {
    await setup();
    expect(screen.getByRole('button', { name: /panel1/i })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /panel2/i })).toBeInTheDocument();
    expect(screen.queryByRole('button', { name: /panel3/i })).not.toBeInTheDocument();
  });

  it('should display children panels', async () => {
    const panelsMockWithChildren = [
      { name: 'parent', label: 'Parent', disabled: false, children: panelsMock },
    ];
    await setup(panelsMockWithChildren, 'parent', '');
    expect(screen.getByRole('button', { name: /parent/i })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /panel1/i })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /panel2/i })).toBeInTheDocument();
  });
});

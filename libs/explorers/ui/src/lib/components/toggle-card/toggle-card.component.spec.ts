import { provideHttpClient } from '@angular/common/http';
import { SvgIconService } from '@sagebionetworks/explorers/services';
import { SvgIconServiceStub } from '@sagebionetworks/explorers/testing';
import { render, screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { ToggleCardComponent, ToggleCardOption } from './toggle-card.component';

const options: ToggleCardOption[] = [
  {
    label: 'Mouse Models',
    value: 'mouse',
    imagePath: '/path/to/mouse.svg',
    imageAltText: 'mouse models icon',
  },
  {
    label: 'Marmoset Models',
    value: 'marmoset',
    imagePath: '/path/to/marmoset.svg',
    imageAltText: 'marmoset models icon',
  },
];

async function setup(value = 'mouse') {
  const user = userEvent.setup();
  const component = await render(ToggleCardComponent, {
    providers: [provideHttpClient(), { provide: SvgIconService, useClass: SvgIconServiceStub }],
    componentInputs: { options, value },
  });
  return { user, component };
}

describe('ToggleCardComponent', () => {
  it('should render a button for each option', async () => {
    await setup();
    expect(screen.getByText('Mouse Models')).toBeInTheDocument();
    expect(screen.getByText('Marmoset Models')).toBeInTheDocument();
  });

  it('should render each option icon', async () => {
    await setup();
    expect(screen.getByAltText('mouse models icon')).toBeInTheDocument();
    expect(screen.getByAltText('marmoset models icon')).toBeInTheDocument();
  });

  it('should emit the selected value when a segment is clicked', async () => {
    const { user, component } = await setup('mouse');
    const valueChange = jest.fn();
    component.fixture.componentInstance.value.subscribe(valueChange);

    await user.click(screen.getByText('Marmoset Models'));

    expect(valueChange).toHaveBeenCalledWith('marmoset');
  });
});

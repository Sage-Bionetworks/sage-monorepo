import { Router } from '@angular/router';
import { render, screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { ResourceCardComponent } from './resource-card.component';

const navigateMock = jest.fn();

const mockTitle = 'Test title';
const mockDescription = 'Test description';

const mockInternalLink = '/internal-link';
const mockExternalLink = 'https://synapse.org';
const mockAltText = 'Test alt text';

describe('ResourceCardComponent', () => {
  async function setup(inputs?: Partial<ResourceCardComponent>) {
    const user = userEvent.setup();
    const component = await render(ResourceCardComponent, {
      componentInputs: {
        imagePath: '/assets/test.svg',
        description: mockDescription,
        link: mockInternalLink,
        altText: mockAltText,
        ...inputs,
      },
      providers: [
        {
          provide: Router,
          useValue: { navigateByUrl: navigateMock },
        },
      ],
    });
    return { user, component };
  }

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render title, description, and image', async () => {
    await setup({ title: mockTitle });

    expect(screen.getByText(mockTitle)).toBeInTheDocument();
    expect(screen.getByText(mockDescription)).toBeInTheDocument();

    const img = screen.getByAltText(mockAltText);
    expect(img).toBeInTheDocument();
  });

  it('should open external link in new tab when links starts with http', async () => {
    const openSpy = jest.spyOn(window, 'open').mockImplementation();

    const { user } = await setup({ link: mockExternalLink });
    const button = screen.getByRole('button', { name: mockDescription });
    await user.click(button);

    expect(openSpy).toHaveBeenCalledWith(mockExternalLink, '_blank');
    openSpy.mockRestore();
  });

  it('should navigate internally when link does not start with http', async () => {
    const { user } = await setup({ link: mockInternalLink });

    const button = screen.getByRole('button', { name: mockDescription });
    await user.click(button);

    expect(navigateMock).toHaveBeenCalledWith(mockInternalLink);
  });
});

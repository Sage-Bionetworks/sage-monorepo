import { render, screen } from '@testing-library/angular';
import { ResourceCardComponent } from './resource-card.component';

const mockTitle = 'Test title';
const mockDescription = 'Test description';

const mockInternalLink = '/internal-link';
const mockExternalLink = 'https://synapse.org';
const mockAltText = 'Test alt text';

describe('ResourceCardComponent', () => {
  async function setup(inputs?: Partial<ResourceCardComponent>) {
    const component = await render(ResourceCardComponent, {
      componentInputs: {
        imagePath: '/assets/test.svg',
        description: mockDescription,
        link: mockInternalLink,
        altText: mockAltText,
        ...inputs,
      },
    });
    return { component };
  }

  it('should render title, description, and image', async () => {
    await setup({ title: mockTitle });

    expect(screen.getByText(mockTitle)).toBeInTheDocument();
    expect(screen.getByText(mockDescription)).toBeInTheDocument();

    const img = screen.getByAltText(mockAltText);
    expect(img).toBeInTheDocument();
  });

  it('should have a link with correct href and target for external link', async () => {
    await setup({ link: mockExternalLink });
    const link = screen.getByRole('link', { name: mockDescription });

    expect(link).toBeInTheDocument();
    expect(link).toHaveAttribute('href', mockExternalLink);
    expect(link).toHaveAttribute('target', '_blank');
  });

  it('should have a link with correct href and target for internal link', async () => {
    await setup({ link: mockInternalLink });
    const link = screen.getByRole('link', { name: mockDescription });

    expect(link).toBeInTheDocument();
    expect(link).toHaveAttribute('href', mockInternalLink);
    expect(link).toHaveAttribute('target', '_blank');
  });
});

import { render, screen } from '@testing-library/angular';
import { ComparisonToolTableLinkComponent } from './comparison-tool-table-link.component';
import { RouterModule } from '@angular/router';

async function setup(linkText = 'Test Link', linkUrl = '/test-url') {
  const { fixture } = await render(ComparisonToolTableLinkComponent, {
    componentInputs: { linkText, linkUrl },
    imports: [RouterModule],
  });
  const component = fixture.componentInstance;
  return { component, fixture };
}

describe('ComparisonToolTableLinkComponent', () => {
  it('should create the component', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });

  it('should render external link with correct text and url', async () => {
    const linkText = 'My Link';
    const linkUrl = 'https://asdf.com/test';
    await setup(linkText, linkUrl);
    const link = screen.getByRole('link', { name: /My Link/i });
    expect(link).toBeInTheDocument();
    expect(link).toHaveAttribute('href', linkUrl);
  });

  it('should render internal link with correct text and url', async () => {
    const linkText = 'My Link';
    const linkUrl = '/my-url';
    await setup(linkText, linkUrl);
    const link = screen.getByRole('link', { name: /My Link/i });
    expect(link).toBeInTheDocument();
    expect(link).toHaveAttribute('href', linkUrl);
  });

  it('should render internal link without leading slash character with correct text and url', async () => {
    const linkText = 'My Link';
    const linkUrl = 'my-url';
    const expectedLinkUrl = '/my-url';
    await setup(linkText, linkUrl);
    const link = screen.getByRole('link', { name: /My Link/i });
    expect(link).toBeInTheDocument();
    expect(link).toHaveAttribute('href', expectedLinkUrl);
  });

  it('should render internal link with parameters correctly', async () => {
    const linkText = 'My Link';
    const linkUrl = 'my-url?param1=value1&param2=value2';
    const expectedLinkUrl = '/my-url?param1=value1&param2=value2';
    await setup(linkText, linkUrl);
    const link = screen.getByRole('link', { name: /My Link/i });
    expect(link).toBeInTheDocument();
    expect(link).toHaveAttribute('href', expectedLinkUrl);
  });
});

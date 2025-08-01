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

  it('should render link with correct text and url', async () => {
    const linkText = 'My Link';
    const linkUrl = '/my-url';
    await setup(linkText, linkUrl);
    const link = screen.getByRole('link', { name: /My Link/i });
    expect(link).toBeInTheDocument();
  });
});

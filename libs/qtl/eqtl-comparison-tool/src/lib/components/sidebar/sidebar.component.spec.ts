import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { render, screen } from '@testing-library/angular';
import { SidebarComponent } from './sidebar.component';

async function setup() {
  return render(SidebarComponent, {
    providers: [provideNoopAnimations()],
  });
}

describe('SidebarComponent', () => {
  it('renders all three chiclets with the expected label/value pairs', async () => {
    await setup();
    expect(screen.getByText('variant:')).toBeInTheDocument();
    expect(screen.getByText(/rs29475839/)).toBeInTheDocument();
    expect(screen.getByText('gene:')).toBeInTheDocument();
    expect(screen.getByText(/PAK1/)).toBeInTheDocument();
    expect(screen.getByText('cell type:')).toBeInTheDocument();
    expect(screen.getByText(/Astrocyte/)).toBeInTheDocument();
  });

  it('renders all three tab headers', async () => {
    await setup();
    expect(screen.getByRole('tab', { name: 'Impact' })).toBeInTheDocument();
    expect(screen.getByRole('tab', { name: 'Traits' })).toBeInTheDocument();
    expect(screen.getByRole('tab', { name: 'Resources' })).toBeInTheDocument();
  });
});

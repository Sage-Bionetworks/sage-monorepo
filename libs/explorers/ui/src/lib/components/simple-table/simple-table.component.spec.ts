import { provideHttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { provideRouter, Router } from '@angular/router';
import { SimpleTableCell, SimpleTableColumn } from '@sagebionetworks/explorers/models';
import { render, screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { SimpleTableComponent } from './simple-table.component';

@Component({ template: '<div>Internal Page</div>' })
class InternalPageComponent {}

async function setup(args: { columns?: SimpleTableColumn[]; rows: SimpleTableCell[][] }) {
  const user = userEvent.setup();
  const component = await render(SimpleTableComponent, {
    providers: [
      provideHttpClient(),
      provideRouter([{ path: 'internal', component: InternalPageComponent }]),
    ],
    componentInputs: {
      columns: args.columns,
      rows: args.rows,
    },
  });
  return { user, component };
}

describe('SimpleTableComponent', () => {
  describe('header', () => {
    it('renders a header row when columns are provided', async () => {
      await setup({
        columns: [{ name: 'TWAS' }, { name: 'Z-Score' }],
        rows: [
          [
            { kind: 'text', value: 'AD' },
            { kind: 'text', value: -20 },
          ],
        ],
      });
      expect(screen.getByRole('columnheader', { name: 'TWAS' })).toBeInTheDocument();
      expect(screen.getByRole('columnheader', { name: 'Z-Score' })).toBeInTheDocument();
    });

    it('does not render a header row when columns are undefined', async () => {
      await setup({
        rows: [
          [
            { kind: 'text', value: 'AD' },
            { kind: 'text', value: -20 },
          ],
        ],
      });
      expect(screen.queryByRole('columnheader')).not.toBeInTheDocument();
    });

    it('marks tooltip-bearing headers with the dotted-underline class', async () => {
      await setup({
        columns: [{ name: 'TWAS' }, { name: 'Z-Score', tooltip: 'A z-score' }],
        rows: [
          [
            { kind: 'text', value: 'AD' },
            { kind: 'text', value: -20 },
          ],
        ],
      });
      const tooltipHeader = screen.getByText('Z-Score');
      expect(tooltipHeader).toHaveClass('dotted-underline');
      expect(screen.getByText('TWAS')).not.toHaveClass('dotted-underline');
    });
  });

  describe('cells', () => {
    it('renders text cells with string and number values', async () => {
      await setup({
        rows: [
          [
            { kind: 'text', value: 'Microglia' },
            { kind: 'text', value: 0.98 },
          ],
        ],
      });
      expect(screen.getByText('Microglia')).toBeInTheDocument();
      expect(screen.getByText('0.98')).toBeInTheDocument();
    });

    it('renders label cells with bold styling', async () => {
      await setup({
        rows: [
          [
            { kind: 'label', text: 'P-Value' },
            { kind: 'text', value: '1.25e-08' },
          ],
        ],
      });
      expect(screen.getByText('P-Value')).toHaveClass('cell-label');
    });

    it('renders internal link cells with routerLink', async () => {
      const { user, component } = await setup({
        rows: [[{ kind: 'link', text: 'Go internal', url: '/internal' }]],
      });
      const link = screen.getByRole('link', { name: 'Go internal' });
      expect(link).toBeInTheDocument();
      const router = component.fixture.debugElement.injector.get(Router);
      await user.click(link);
      expect(router.url).toBe('/internal');
    });

    it('renders external link cells with target=_blank', async () => {
      await setup({
        rows: [
          [
            {
              kind: 'link',
              text: 'External',
              url: 'https://example.com',
            },
          ],
        ],
      });
      const link = screen.getByRole('link', { name: 'External' });
      expect(link).toHaveAttribute('href', 'https://example.com');
      expect(link).toHaveAttribute('target', '_blank');
    });

    it('renders image cells with src and alt', async () => {
      await setup({
        rows: [
          [
            { kind: 'image', src: '/img.png', alt: 'Logo' },
            { kind: 'text', value: 'Caption' },
          ],
        ],
      });
      const img = screen.getByAltText('Logo');
      expect(img).toBeInTheDocument();
      expect(img).toHaveAttribute('src', '/img.png');
    });
  });

  describe('combinations', () => {
    it('supports a header with mixed image/link/label/text cells in the body', async () => {
      await setup({
        columns: [{ name: 'Source' }, { name: 'Detail' }, { name: 'Score', align: 'right' }],
        rows: [
          [
            { kind: 'image', src: '/agora.svg', alt: 'Agora' },
            { kind: 'link', text: 'View', url: '/agora' },
            { kind: 'text', value: 0.98 },
          ],
          [
            { kind: 'label', text: 'Manual' },
            { kind: 'text', value: 'Curated' },
            { kind: 'text', value: 0.5 },
          ],
        ],
      });
      expect(screen.getByRole('columnheader', { name: 'Source' })).toBeInTheDocument();
      expect(screen.getByAltText('Agora')).toBeInTheDocument();
      expect(screen.getByRole('link', { name: 'View' })).toBeInTheDocument();
      expect(screen.getByText('Manual')).toHaveClass('cell-label');
      expect(screen.getByText('0.98')).toBeInTheDocument();
    });
  });
});

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
            { type: 'text', value: 'AD' },
            { type: 'text', value: -20 },
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
            { type: 'text', value: 'AD' },
            { type: 'text', value: -20 },
          ],
        ],
      });
      expect(screen.queryByRole('columnheader')).not.toBeInTheDocument();
    });

    it('applies the column className to the matching header and body cells', async () => {
      const { component } = await setup({
        columns: [{ name: 'Abbreviation' }, { name: 'Description', className: 'description-col' }],
        rows: [
          [
            { type: 'text', value: 'AD' },
            { type: 'text', value: 'Alzheimer disease' },
          ],
        ],
      });
      const headers = component.container.querySelectorAll<HTMLElement>('thead th');
      const cells = component.container.querySelectorAll<HTMLElement>('tbody td');
      expect(headers[0]).not.toHaveClass('description-col');
      expect(headers[1]).toHaveClass('description-col');
      expect(cells[0]).not.toHaveClass('description-col');
      expect(cells[1]).toHaveClass('description-col');
    });

    it('marks tooltip-bearing headers with the dotted-underline class', async () => {
      await setup({
        columns: [{ name: 'TWAS' }, { name: 'Z-Score', tooltip: 'A z-score' }],
        rows: [
          [
            { type: 'text', value: 'AD' },
            { type: 'text', value: -20 },
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
            { type: 'text', value: 'Microglia' },
            { type: 'text', value: 0.98 },
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
            { type: 'label', text: 'P-Value' },
            { type: 'text', value: '1.25e-08' },
          ],
        ],
      });
      expect(screen.getByText('P-Value')).toHaveClass('cell-label');
    });

    it('renders internal link cells with routerLink', async () => {
      const { user, component } = await setup({
        rows: [[{ type: 'link', text: 'Go internal', href: '/internal' }]],
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
              type: 'link',
              text: 'External',
              href: 'https://example.com',
            },
          ],
        ],
      });
      const link = screen.getByRole('link', { name: 'External' });
      expect(link).toHaveAttribute('href', 'https://example.com');
      expect(link).toHaveAttribute('target', '_blank');
    });

    it('renders swatch cells with a color square and text', async () => {
      const { component } = await setup({
        rows: [[{ type: 'swatch', color: '#bd2438', text: 'Immune' }]],
      });
      expect(screen.getByText('Immune')).toBeInTheDocument();
      const swatchColor = component.container.querySelector<HTMLElement>('.cell-swatch-color');
      expect(swatchColor).not.toBeNull();
      expect(swatchColor?.style.backgroundColor).toBe('rgb(189, 36, 56)');
    });

    it('renders image cells with src and alt', async () => {
      await setup({
        rows: [
          [
            { type: 'image', src: '/img.png', alt: 'Logo' },
            { type: 'text', value: 'Caption' },
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
        columns: [{ name: 'Source' }, { name: 'Detail' }, { name: 'Score' }],
        rows: [
          [
            { type: 'image', src: '/agora.svg', alt: 'Agora' },
            { type: 'link', text: 'View', href: '/agora' },
            { type: 'text', value: 0.98 },
          ],
          [
            { type: 'label', text: 'Manual' },
            { type: 'text', value: 'Curated' },
            { type: 'text', value: 0.5 },
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

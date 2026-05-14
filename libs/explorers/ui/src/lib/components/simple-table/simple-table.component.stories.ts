import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideLocationMocks } from '@angular/common/testing';
import { provideRouter } from '@angular/router';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { SimpleTableComponent } from './simple-table.component';

const meta: Meta<SimpleTableComponent> = {
  component: SimpleTableComponent,
  title: 'UI/SimpleTableComponent',
  decorators: [
    applicationConfig({
      providers: [
        provideRouter([]),
        provideLocationMocks(),
        provideHttpClient(withInterceptorsFromDi()),
      ],
    }),
  ],
};
export default meta;
type Story = StoryObj<SimpleTableComponent>;

export const WithHeader: Story = {
  args: {
    columns: [
      { name: 'TWAS' },
      {
        name: 'Z-Score',
        tooltip: 'More negative is better',
      },
    ],
    rows: [
      [
        { type: 'link', text: "Alzheimer's Disease", href: '/diseases/alzheimers' },
        { type: 'text', value: -20 },
      ],
      [
        { type: 'link', text: 'Schizophrenia', href: '/diseases/schizophrenia' },
        { type: 'text', value: -19.0591 },
      ],
      [
        { type: 'link', text: 'Depression', href: '/diseases/depression' },
        { type: 'text', value: -18.9775 },
      ],
      [
        { type: 'link', text: "Tourette's Syndrome", href: '/diseases/tourettes' },
        { type: 'text', value: -16.016 },
      ],
    ],
  },
};

export const WithImages: Story = {
  args: {
    columns: undefined,
    columnGap: '10px',
    rows: [
      [
        {
          type: 'image',
          src: 'qtl-assets/images/agora-logomark-only.svg',
          alt: 'Agora icon',
        },
        {
          type: 'link',
          text: 'View this gene on Agora',
          href: 'https://agora.adknowledgeportal.org',
        },
      ],
      [
        {
          type: 'image',
          src: 'qtl-assets/images/ucsc-genome-browser-logo.svg',
          alt: 'UCSC icon',
        },
        {
          type: 'link',
          text: 'View this variant on the UCSC Genome Browser',
          href: 'https://genome.ucsc.edu',
        },
      ],
      [
        {
          type: 'image',
          src: 'qtl-assets/images/czi-logo.svg',
          alt: 'CZI icon',
        },
        {
          type: 'link',
          text: 'View UMAP diagrams on CZI',
          href: 'https://cellxgene.cziscience.com',
        },
      ],
    ],
  },
  render: (args) => ({
    props: args,
    template: `
      <div style="--simple-table-font-size: 14px; --simple-table-line-height: 17px; --simple-table-text-color: #4A5056; --simple-table-row-padding: 16px;">
        <explorers-simple-table [rows]="rows" [columnGap]="columnGap" />
      </div>
    `,
  }),
};

export const WithoutHeader: Story = {
  args: {
    columns: undefined,
    rows: [
      [
        { type: 'label', text: 'Correlation' },
        { type: 'text', value: '.98' },
      ],
      [
        { type: 'label', text: 'P-Value' },
        { type: 'text', value: '1.25e-08' },
      ],
      [
        { type: 'label', text: 'Variant Loc.' },
        { type: 'text', value: 'chr11:54455-252455' },
      ],
      [
        { type: 'label', text: 'Gene Loc.' },
        { type: 'text', value: 'chr11:54455-252455' },
      ],
      [
        { type: 'label', text: 'Cell Type' },
        { type: 'text', value: 'Microglia' },
      ],
    ],
  },
  render: (args) => ({
    props: args,
    template: `
      <div style="--simple-table-font-size: 14px; --simple-table-line-height: 17px; --simple-table-text-color: #4A5056; --simple-table-row-padding: 12px;">
        <explorers-simple-table [rows]="rows" />
      </div>
    `,
  }),
};

export const WithSwatch: Story = {
  args: {
    columns: [{ name: 'Abbreviation' }, { name: 'Description' }],
    rows: [
      [
        { type: 'swatch', color: '#bd2438', text: 'Immune' },
        { type: 'text', value: 'Immune Cell' },
      ],
      [
        { type: 'swatch', color: '#c7a87c', text: 'Astro' },
        { type: 'text', value: 'Astrocyte' },
      ],
      [
        { type: 'swatch', color: '#f0c419', text: 'OPC' },
        { type: 'text', value: 'Oligodendrocyte Progenitor Cell' },
      ],
    ],
    columnGap: '40px',
  },
};

export const Combined: Story = {
  args: {
    columns: [
      { name: 'Source' },
      { name: 'Description' },
      { name: 'Score', tooltip: 'Higher is better.' },
    ],
    rows: [
      [
        {
          type: 'image',
          src: 'explorers-assets/images/gene-comparison-icon.svg',
          alt: 'Agora icon',
        },
        { type: 'link', text: 'View on Agora', href: '/agora' },
        { type: 'text', value: 0.98 },
      ],
      [
        { type: 'label', text: 'Manual' },
        { type: 'text', value: 'Curated by the QTL team.' },
        { type: 'text', value: 0.75 },
      ],
      [
        {
          type: 'image',
          src: 'explorers-assets/images/gene-search-icon.svg',
          alt: 'UCSC icon',
        },
        {
          type: 'link',
          text: 'UCSC Genome Browser',
          href: 'https://genome.ucsc.edu',
        },
        { type: 'text', value: 0.5 },
      ],
    ],
  },
};

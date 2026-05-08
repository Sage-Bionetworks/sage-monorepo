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
        align: 'right',
      },
    ],
    rows: [
      [
        { kind: 'link', text: "Alzheimer's Disease", url: '/diseases/alzheimers' },
        { kind: 'text', value: -20 },
      ],
      [
        { kind: 'link', text: 'Schizophrenia', url: '/diseases/schizophrenia' },
        { kind: 'text', value: -19.0591 },
      ],
      [
        { kind: 'link', text: 'Depression', url: '/diseases/depression' },
        { kind: 'text', value: -18.9775 },
      ],
      [
        { kind: 'link', text: "Tourette's Syndrome", url: '/diseases/tourettes' },
        { kind: 'text', value: -16.016 },
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
          kind: 'image',
          src: 'qtl-assets/images/agora-logomark-only.svg',
          alt: 'Agora icon',
        },
        {
          kind: 'link',
          text: 'View this gene on Agora',
          url: 'https://agora.adknowledgeportal.org',
        },
      ],
      [
        {
          kind: 'image',
          src: 'qtl-assets/images/ucsc-genome-browser-logo.svg',
          alt: 'UCSC icon',
        },
        {
          kind: 'link',
          text: 'View this variant on the UCSC Genome Browser',
          url: 'https://genome.ucsc.edu',
        },
      ],
      [
        {
          kind: 'image',
          src: 'qtl-assets/images/czi-logo.svg',
          alt: 'CZI icon',
        },
        {
          kind: 'link',
          text: 'View UMAP diagrams on CZI',
          url: 'https://cellxgene.cziscience.com',
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
        { kind: 'label', text: 'Correlation' },
        { kind: 'text', value: '.98' },
      ],
      [
        { kind: 'label', text: 'P-Value' },
        { kind: 'text', value: '1.25e-08' },
      ],
      [
        { kind: 'label', text: 'Variant Loc.' },
        { kind: 'text', value: 'chr11:54455-252455' },
      ],
      [
        { kind: 'label', text: 'Gene Loc.' },
        { kind: 'text', value: 'chr11:54455-252455' },
      ],
      [
        { kind: 'label', text: 'Cell Type' },
        { kind: 'text', value: 'Microglia' },
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

export const Combined: Story = {
  args: {
    columns: [
      { name: 'Source' },
      { name: 'Description' },
      { name: 'Score', tooltip: 'Higher is better.', align: 'right' },
    ],
    rows: [
      [
        {
          kind: 'image',
          src: 'explorers-assets/images/gene-comparison-icon.svg',
          alt: 'Agora icon',
        },
        { kind: 'link', text: 'View on Agora', url: '/agora' },
        { kind: 'text', value: 0.98 },
      ],
      [
        { kind: 'label', text: 'Manual' },
        { kind: 'text', value: 'Curated by the QTL team.' },
        { kind: 'text', value: 0.75 },
      ],
      [
        {
          kind: 'image',
          src: 'explorers-assets/images/gene-search-icon.svg',
          alt: 'UCSC icon',
        },
        {
          kind: 'link',
          text: 'UCSC Genome Browser',
          url: 'https://genome.ucsc.edu',
        },
        { kind: 'text', value: 0.5 },
      ],
    ],
  },
};

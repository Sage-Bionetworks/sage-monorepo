import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideLocationMocks } from '@angular/common/testing';
import { provideRouter } from '@angular/router';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { StatCardsComponent } from './stat-cards.component';

const meta: Meta<StatCardsComponent> = {
  component: StatCardsComponent,
  title: 'UI/Cards/StatCardsComponent',
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
type Story = StoryObj<StatCardsComponent>;

const icon = 'explorers-assets/icons/people.svg';
const link = '/';

const fourCards = [
  {
    iconPath: icon,
    iconAltText: 'icon',
    header: 'Total QTLs',
    subHeader: 'across 53 tissues',
    link,
  },
  { iconPath: icon, iconAltText: 'icon', header: 'Studies', subHeader: 'AD cohorts', link },
  { iconPath: icon, iconAltText: 'icon', header: 'Variant–gene associations', link },
  { iconPath: icon, iconAltText: 'icon', header: 'Tissues covered', link },
];

const coloredCards = [
  {
    iconPath: 'explorers-assets/icons/people.svg',
    iconAltText: 'people',
    header: '4000',
    subHeader: 'Brain Donors',
    iconBackgroundColor: '#2C5182',
    iconColor: 'white',
    link,
  },
  {
    iconPath: 'explorers-assets/icons/cell.svg',
    iconAltText: 'cell',
    header: '81',
    subHeader: 'Cell Types',
    iconBackgroundColor: '#388C95',
    iconColor: 'white',
    link,
  },
  {
    iconPath: 'explorers-assets/icons/gene-search.svg',
    iconAltText: 'gene search',
    header: '3',
    subHeader: 'Ancestries',
    iconBackgroundColor: '#A5C7F3',
    iconColor: 'white',
    link,
  },
  {
    iconPath: 'explorers-assets/icons/erythrocytes.svg',
    iconAltText: 'erythrocytes',
    header: '2 million',
    subHeader: 'Single Cells',
    iconBackgroundColor: '#6E76AE',
    iconColor: 'white',
    link,
  },
  {
    iconPath: 'explorers-assets/icons/eqtl.svg',
    iconAltText: 'eQTL',
    header: '252 million',
    subHeader: 'eQTLs',
    iconBackgroundColor: '#D7AE0C',
    iconColor: 'white',
    link,
  },
];

export const FourCards: Story = {
  args: {
    cards: fourCards,
  },
};

export const SingleCard: Story = {
  args: {
    cards: [
      {
        iconPath: icon,
        iconAltText: 'icon',
        header: 'Total QTLs',
        subHeader: 'across 53 tissues',
        link,
      },
    ],
  },
};

export const SevenCards: Story = {
  args: {
    cards: [
      ...fourCards,
      { iconPath: icon, iconAltText: 'icon', header: 'Cohorts', link },
      { iconPath: icon, iconAltText: 'icon', header: 'Data types', link },
      { iconPath: icon, iconAltText: 'icon', header: 'Genes', link },
    ],
  },
};

export const EightCards: Story = {
  args: {
    cards: [
      ...fourCards,
      { iconPath: icon, iconAltText: 'icon', header: 'Cohorts', link },
      { iconPath: icon, iconAltText: 'icon', header: 'Data types', link },
      { iconPath: icon, iconAltText: 'icon', header: 'Genes', link },
      { iconPath: icon, iconAltText: 'icon', header: 'QC pass rate', link },
    ],
  },
};

export const ColoredIconCircles: Story = {
  args: {
    cards: coloredCards,
  },
};

export const AnimateOnLoad: Story = {
  args: {
    cards: coloredCards,
    animateOnLoad: true,
  },
};

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

const fourCards = [
  { iconPath: icon, header: 'Total QTLs', subHeader: 'across 53 tissues' },
  { iconPath: icon, header: 'Studies', subHeader: 'AD cohorts' },
  { iconPath: icon, header: 'Variant–gene associations' },
  { iconPath: icon, header: 'Tissues covered' },
];

export const FourCards: Story = {
  args: {
    cards: fourCards,
  },
};

export const SingleCard: Story = {
  args: {
    cards: [{ iconPath: icon, header: 'Total QTLs', subHeader: 'across 53 tissues' }],
  },
};

export const SevenCards: Story = {
  args: {
    cards: [
      ...fourCards,
      { iconPath: icon, header: 'Cohorts' },
      { iconPath: icon, header: 'Data types' },
      { iconPath: icon, header: 'Genes' },
    ],
  },
};

export const EightCards: Story = {
  args: {
    cards: [
      ...fourCards,
      { iconPath: icon, header: 'Cohorts' },
      { iconPath: icon, header: 'Data types' },
      { iconPath: icon, header: 'Genes' },
      { iconPath: icon, header: 'QC pass rate' },
    ],
  },
};

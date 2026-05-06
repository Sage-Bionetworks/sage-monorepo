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

const fourCards = [
  { value: '1,234', label: 'Total QTLs' },
  { value: '42', label: 'Studies' },
  { value: '8.6M', label: 'Variant–gene associations' },
  { value: '53', label: 'Tissues covered' },
];

export const FourCards: Story = {
  args: {
    cards: fourCards,
  },
};

export const SingleCard: Story = {
  args: {
    cards: [{ value: '1,234', label: 'Total QTLs' }],
  },
};

export const EightCards: Story = {
  args: {
    cards: [
      ...fourCards,
      { value: '120', label: 'Cohorts' },
      { value: '7', label: 'Data types' },
      { value: '3.2k', label: 'Genes' },
      { value: '99.9%', label: 'QC pass rate' },
    ],
  },
};

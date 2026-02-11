import { geneMock1 } from '@sagebionetworks/agora/testing';
import { type Meta, type StoryObj } from '@storybook/angular';
import { GeneHeroComponent } from './gene-hero.component';

const meta: Meta<GeneHeroComponent> = {
  component: GeneHeroComponent,
  title: 'Gene Comparison Tool/GeneHero',
};
export default meta;
type Story = StoryObj<GeneHeroComponent>;

export const GeneHero: Story = {
  args: {
    gene: geneMock1,
  },
};

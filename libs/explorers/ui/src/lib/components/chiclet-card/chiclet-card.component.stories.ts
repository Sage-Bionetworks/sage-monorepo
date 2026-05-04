import type { Meta, StoryObj } from '@storybook/angular';
import { ChicletCardComponent } from './chiclet-card.component';

const meta: Meta<ChicletCardComponent> = {
  component: ChicletCardComponent,
  title: 'UI/Cards/ChicletCardComponent',
};
export default meta;
type Story = StoryObj<ChicletCardComponent>;

export const Default: Story = {
  args: {
    title: 'Example searches',
    chiclets: [
      { label: 'PAK1', color: '#4caf50' },
      { label: 'chr1:109.8Mb', color: '#3f51b5' },
      { label: 'rs1801133', color: '#009688' },
      { label: 'rs6265', color: '#009688' },
      { label: 'APOE', color: '#9c27b0' },
      { label: 'chrX:73.5Mb', color: '#4caf50' },
      { label: 'GATA3, FADS1, SLC30A8', color: '#009688' },
      { label: 'chr2:164.3Mb', color: '#3f51b5' },
      { label: 'chr2:164.3Mb', color: '#4caf50' },
      { label: 'rs38090111, rs48102218', color: '#009688' },
      { label: 'DRD2', color: '#9c27b0' },
    ],
  },
};

export const FewChiclets: Story = {
  args: {
    title: 'Gene searches',
    chiclets: [
      { label: 'APOE', color: '#9c27b0' },
      { label: 'BRCA1', color: '#4caf50' },
      { label: 'TP53', color: '#3f51b5' },
    ],
  },
};

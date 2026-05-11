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
      { text: 'PAK1', backgroundColor: '#4caf50' },
      { text: 'chr1:109.8Mb', backgroundColor: '#3f51b5' },
      { text: 'rs1801133', backgroundColor: '#009688' },
      { text: 'rs6265', backgroundColor: '#009688' },
      { text: 'APOE', backgroundColor: '#9c27b0' },
      { text: 'chrX:73.5Mb', backgroundColor: '#4caf50' },
      { text: 'GATA3, FADS1, SLC30A8', backgroundColor: '#009688' },
      { text: 'chr2:164.3Mb', backgroundColor: '#3f51b5' },
      { text: 'chr2:164.3Mb', backgroundColor: '#4caf50' },
      { text: 'rs38090111, rs48102218', backgroundColor: '#009688' },
      { text: 'DRD2', backgroundColor: '#9c27b0' },
    ],
  },
};

export const FewChiclets: Story = {
  args: {
    title: 'Gene searches',
    chiclets: [
      { text: 'APOE', backgroundColor: '#9c27b0' },
      { text: 'BRCA1', backgroundColor: '#4caf50' },
      { text: 'TP53', backgroundColor: '#3f51b5' },
    ],
  },
};

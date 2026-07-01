import { provideLocationMocks } from '@angular/common/testing';
import { provideRouter } from '@angular/router';
import { drugMock } from '@sagebionetworks/agora/testing';
import { applicationConfig } from '@storybook/angular';
import { type Meta, type StoryObj } from '@storybook/angular';
import { DrugDetailsHeroComponent } from './drug-details-hero.component';

const meta: Meta<DrugDetailsHeroComponent> = {
  component: DrugDetailsHeroComponent,
  title: 'DrugDetails/DrugDetailsHero',
  decorators: [
    applicationConfig({
      providers: [provideRouter([]), provideLocationMocks()],
    }),
  ],
};
export default meta;
type Story = StoryObj<DrugDetailsHeroComponent>;

export const DrugDetailsHero: Story = {
  args: {
    drug: drugMock,
  },
};

export const MultiDrugCombinations: Story = {
  args: {
    drug: {
      ...drugMock,
      drug_nominations: [
        drugMock.drug_nominations[0],
        {
          ...drugMock.drug_nominations[1],
          combined_with: [
            { common_name: 'Letrozole', chembl_id: 'CHEMBL1' },
            { common_name: 'Pharmatanium', chembl_id: 'CHEMBL2' },
          ],
        },
        {
          ...drugMock.drug_nominations[1],
          combined_with: [
            { common_name: 'Letrozole', chembl_id: 'CHEMBL1' },
            { common_name: 'Pharmatanium', chembl_id: 'CHEMBL2' },
            { common_name: 'Unpharmatanium', chembl_id: 'CHEMBL3' },
          ],
        },
      ],
    },
  },
};

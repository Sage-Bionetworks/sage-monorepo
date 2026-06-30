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

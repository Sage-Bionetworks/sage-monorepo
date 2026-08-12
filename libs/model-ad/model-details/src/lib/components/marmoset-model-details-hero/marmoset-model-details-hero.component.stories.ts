import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideLocationMocks } from '@angular/common/testing';
import { provideRouter } from '@angular/router';
import { marmosetModelMock } from '@sagebionetworks/model-ad/testing';
import { applicationConfig, type Meta, type StoryObj } from '@storybook/angular';
import { MarmosetModelDetailsHeroComponent } from './marmoset-model-details-hero.component';

const meta: Meta<MarmosetModelDetailsHeroComponent> = {
  component: MarmosetModelDetailsHeroComponent,
  title: 'Model Details/Marmoset/MarmosetModelDetailsHeroComponent',
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
type Story = StoryObj<MarmosetModelDetailsHeroComponent>;

export const Default: Story = {
  args: {
    model: marmosetModelMock,
  },
};

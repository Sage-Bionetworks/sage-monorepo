import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideLocationMocks } from '@angular/common/testing';
import { provideRouter } from '@angular/router';
import { mouseModelMock } from '@sagebionetworks/model-ad/testing';
import { applicationConfig, type Meta, type StoryObj } from '@storybook/angular';
import { MouseModelDetailsHeroComponent } from './mouse-model-details-hero.component';

const meta: Meta<MouseModelDetailsHeroComponent> = {
  component: MouseModelDetailsHeroComponent,
  title: 'Model Details/Mouse/MouseModelDetailsHeroComponent',
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
type Story = StoryObj<MouseModelDetailsHeroComponent>;

export const Default: Story = {
  args: {
    model: mouseModelMock,
  },
};

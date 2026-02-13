import { CommonModule } from '@angular/common';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { WikiComponent } from '@sagebionetworks/agora/shared';
import { applicationConfig, moduleMetadata, type Meta, type StoryObj } from '@storybook/angular';
import { AboutComponent } from './about.component';

const meta: Meta<AboutComponent> = {
  component: AboutComponent,
  title: 'Pages/About',
  decorators: [
    applicationConfig({
      providers: [provideHttpClient(withInterceptorsFromDi())],
    }),
    moduleMetadata({
      imports: [CommonModule, WikiComponent],
    }),
  ],
};
export default meta;
type Story = StoryObj<AboutComponent>;

export const About: Story = {
  args: {},
};

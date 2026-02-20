import { CommonModule } from '@angular/common';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { WikiComponent } from '@sagebionetworks/explorers/util';
import { applicationConfig, moduleMetadata, type Meta, type StoryObj } from '@storybook/angular';
import { NotFoundComponent } from './not-found.component';

const meta: Meta<NotFoundComponent> = {
  component: NotFoundComponent,
  title: 'Pages/NotFound',
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
type Story = StoryObj<NotFoundComponent>;

export const NotFound: Story = {
  args: {},
};

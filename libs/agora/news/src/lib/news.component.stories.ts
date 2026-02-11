import { CommonModule } from '@angular/common';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { WikiComponent } from '@sagebionetworks/agora/shared';
import { applicationConfig, moduleMetadata, type Meta, type StoryObj } from '@storybook/angular';
import { NewsComponent } from './news.component';

const meta: Meta<NewsComponent> = {
  component: NewsComponent,
  title: 'News',
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
type Story = StoryObj<NewsComponent>;

export const Primary: Story = {
  args: {},
};

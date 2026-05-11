import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { FilterChicletComponent } from './filter-chiclet.component';

const meta: Meta<FilterChicletComponent> = {
  component: FilterChicletComponent,
  title: 'UI/Chiclets/FilterChicletComponent',
  decorators: [
    applicationConfig({
      providers: [provideHttpClient(withInterceptorsFromDi())],
    }),
  ],
};
export default meta;
type Story = StoryObj<FilterChicletComponent>;

export const Default: Story = {
  args: { name: 'biodomain', value: 'Synapse' },
};

export const ValueOnly: Story = {
  args: { value: 'Significance ≤ 0.05' },
};

import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { ComparisonToolHeaderComponent } from './comparison-tool-header.component';

const meta: Meta<ComparisonToolHeaderComponent> = {
  component: ComparisonToolHeaderComponent,
  title: 'Comparison Tools/ComparisonToolHeaderComponent',
  decorators: [
    applicationConfig({
      providers: [provideRouter([]), provideHttpClient(withInterceptorsFromDi())],
    }),
  ],
};
export default meta;
type Story = StoryObj<ComparisonToolHeaderComponent>;

export const Demo: Story = {
  args: {
    headerTitle: 'Gene Comparison',
    filterResultsButtonTooltip: 'Filter the results based on the selected criteria',
  },
};

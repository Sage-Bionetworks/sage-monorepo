import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideLocationMocks } from '@angular/common/testing';
import { provideRouter } from '@angular/router';
import { Panel } from '@sagebionetworks/explorers/models';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { PanelNavigationComponent } from './panel-navigation.component';

const meta: Meta<PanelNavigationComponent> = {
  component: PanelNavigationComponent,
  title: 'UI/PanelNavigationComponent',
  decorators: [
    applicationConfig({
      providers: [
        provideRouter([]),
        provideLocationMocks(),
        provideHttpClient(withInterceptorsFromDi()),
      ],
    }),
  ],
  argTypes: {
    panelChange: { control: false },
  },
};
export default meta;
type Story = StoryObj<PanelNavigationComponent>;

export const Demo: Story = {
  args: {
    panels: [
      {
        name: 'summary',
        label: 'Summary',
        disabled: false,
      },
      {
        name: 'evidence',
        label: 'Evidence',
        disabled: false,
        children: [
          {
            name: 'rna',
            label: 'RNA',
            disabled: false,
          },
          {
            name: 'protein',
            label: 'Protein',
            disabled: false,
          },
          {
            name: 'metabolomics',
            label: 'Metabolomics',
            disabled: false,
          },
        ],
      },
      {
        name: 'resources',
        label: 'Resources',
        disabled: false,
      },
      {
        name: 'nominations',
        label: 'Nomination Details',
        disabled: false,
      },
      {
        name: 'experimental-validation',
        label: 'Experimental Validation',
        disabled: false,
      },
    ],
    activePanel: 'summary',
    activeParent: '',
    panelChange: (panel: Panel) => {
      console.log(panel);
    },
  },
};

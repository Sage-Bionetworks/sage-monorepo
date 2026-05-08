import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { SvgIconService } from '@sagebionetworks/explorers/services';
import { SvgIconServiceStub } from '@sagebionetworks/explorers/testing';
import { applicationConfig } from '@storybook/angular';
import type { Meta, StoryObj } from '@storybook/angular';
import { ChicletComponent } from './chiclet.component';

const meta: Meta<ChicletComponent> = {
  component: ChicletComponent,
  title: 'UI/ChicletComponent',
  decorators: [
    applicationConfig({
      providers: [
        provideHttpClient(withInterceptorsFromDi()),
        { provide: SvgIconService, useClass: SvgIconServiceStub },
      ],
    }),
  ],
};
export default meta;
type Story = StoryObj<ChicletComponent>;

export const Default: Story = {
  args: { label: 'APOE' },
};

export const WithBackgroundColor: Story = {
  args: { label: 'PAK1', backgroundColor: '#4caf50' },
};

export const WithTextColor: Story = {
  args: { label: 'PAK1', backgroundColor: '#4caf50', textColor: 'white' },
};

export const WithFontSize: Story = {
  args: { label: 'APOE', fontSize: '14px' },
};

export const WithBoldPrefix: Story = {
  args: { label: '<b>biodomain:</b>&nbsp;Synapse' },
};

export const Removable: Story = {
  args: { label: 'sex: Female', removable: true },
};

export const RemovableWithCloseIconColor: Story = {
  args: { label: 'sex: Female', removable: true, closeIconColor: '#5081a7' },
};

export const RemovableWithCloseIconSize: Story = {
  args: { label: 'sex: Female', removable: true, closeIconSize: 10 },
};

export const RemovableWithCustomAriaLabel: Story = {
  args: { label: 'sex: Female', removable: true, removeAriaLabel: 'Clear sex: Female' },
};

export const Playground: Story = {
  args: {
    label: '<b>biodomain:</b>&nbsp;Synapse',
    backgroundColor: '#3f51b5',
    textColor: 'white',
    closeIconColor: 'white',
    closeIconSize: 10,
    fontSize: '14px',
    removable: true,
  },
};

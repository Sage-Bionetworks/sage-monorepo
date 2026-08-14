import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideLocationMocks } from '@angular/common/testing';
import { Component, signal } from '@angular/core';
import { provideRouter } from '@angular/router';
import { Panel } from '@sagebionetworks/explorers/models';
import { mouseModelMock } from '@sagebionetworks/model-ad/testing';
import { applicationConfig, type Meta, type StoryObj } from '@storybook/angular';
import { MouseModelDetailsContentComponent } from './mouse-model-details-content.component';
import { getPanels, getPanelsWithDisabledState } from './mouse-model-details-panels';

@Component({
  selector: 'model-ad-mouse-model-details-content-story-wrapper',
  imports: [MouseModelDetailsContentComponent],
  template: `
    <model-ad-mouse-model-details-content
      [model]="model"
      [panels]="panels"
      [activePanel]="activePanel()"
      [activeParent]="''"
      [scrollToPanelNavElementOnInitialLoad]="false"
      (panelChange)="onPanelChange($event)"
    />
  `,
})
class MouseModelDetailsContentStoryWrapperComponent {
  readonly model = mouseModelMock;
  readonly panels = getPanelsWithDisabledState(mouseModelMock, getPanels());
  readonly activePanel = signal('omics');

  onPanelChange(panel: Panel) {
    this.activePanel.set(panel.name);
  }
}

const meta: Meta<MouseModelDetailsContentStoryWrapperComponent> = {
  component: MouseModelDetailsContentStoryWrapperComponent,
  title: 'Model Details/Mouse/MouseModelDetailsContentComponent',
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
type Story = StoryObj<MouseModelDetailsContentStoryWrapperComponent>;

export const Default: Story = {};

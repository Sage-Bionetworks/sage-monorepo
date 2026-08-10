import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideLocationMocks } from '@angular/common/testing';
import { Component, signal } from '@angular/core';
import { provideRouter } from '@angular/router';
import { Panel } from '@sagebionetworks/explorers/models';
import { marmosetModelMock } from '@sagebionetworks/model-ad/testing';
import { applicationConfig, type Meta, type StoryObj } from '@storybook/angular';
import { MarmosetModelDetailsContentComponent } from './marmoset-model-details-content.component';
import { getPanels, getPanelsWithDisabledState } from './marmoset-model-details-panels';

@Component({
  selector: 'model-ad-marmoset-model-details-content-story-wrapper',
  imports: [MarmosetModelDetailsContentComponent],
  template: `
    <model-ad-marmoset-model-details-content
      [model]="model"
      [panels]="panels"
      [activePanel]="activePanel()"
      [activeParent]="''"
      [scrollToPanelNavElementOnInitialLoad]="false"
      (panelChange)="onPanelChange($event)"
    />
  `,
})
class MarmosetModelDetailsContentStoryWrapperComponent {
  readonly model = marmosetModelMock;
  readonly panels = getPanelsWithDisabledState(marmosetModelMock, getPanels());
  readonly activePanel = signal('biomarkers');

  onPanelChange(panel: Panel) {
    this.activePanel.set(panel.name);
  }
}

const meta: Meta<MarmosetModelDetailsContentStoryWrapperComponent> = {
  component: MarmosetModelDetailsContentStoryWrapperComponent,
  title: 'Model Details/Marmoset/MarmosetModelDetailsContentComponent',
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
type Story = StoryObj<MarmosetModelDetailsContentStoryWrapperComponent>;

export const Default: Story = {};

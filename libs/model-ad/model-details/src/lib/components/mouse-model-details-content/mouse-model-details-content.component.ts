import { Component, input, output } from '@angular/core';
import { Panel, SynapseWikiParams } from '@sagebionetworks/explorers/models';
import { PanelNavigationComponent } from '@sagebionetworks/explorers/ui';
import { MouseModel } from '@sagebionetworks/model-ad/api-client';
import { ModelDetailsHeroComponent } from '../model-details-hero/model-details-hero.component';
import { ModelDetailsOmicsComponent } from '../model-details-omics/model-details-omics.component';
import { ModelDetailsResourcesComponent } from '../model-details-resources/model-details-resources.component';
import { MouseModelDetailsBoxplotsSelectorComponent } from '../mouse-model-details-boxplots-selector/mouse-model-details-boxplots-selector.component';

@Component({
  selector: 'model-ad-mouse-model-details-content',
  imports: [
    PanelNavigationComponent,
    ModelDetailsHeroComponent,
    ModelDetailsOmicsComponent,
    ModelDetailsResourcesComponent,
    MouseModelDetailsBoxplotsSelectorComponent,
  ],
  templateUrl: './mouse-model-details-content.component.html',
  styleUrls: ['./mouse-model-details-content.component.scss'],
})
export class MouseModelDetailsContentComponent {
  model = input.required<MouseModel>();
  panels = input.required<Panel[]>();
  activePanel = input.required<string>();
  activeParent = input.required<string>();
  scrollToPanelNavElementOnInitialLoad = input.required<boolean>();
  panelChange = output<Panel>();

  biomarkersWikiParams: SynapseWikiParams = { ownerId: 'syn66271427', wikiId: '632871' };
  pathologyWikiParams: SynapseWikiParams = { ownerId: 'syn66271427', wikiId: '632872' };
}

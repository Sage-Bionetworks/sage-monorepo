import { Component, input, output } from '@angular/core';
import { Panel, SynapseWikiParams } from '@sagebionetworks/explorers/models';
import { PanelNavigationComponent } from '@sagebionetworks/explorers/ui';
import { MarmosetModel } from '@sagebionetworks/model-ad/api-client';
import { MarmosetModelDetailsBoxplotsSelectorComponent } from '../marmoset-model-details-boxplots-selector/marmoset-model-details-boxplots-selector.component';

@Component({
  selector: 'model-ad-marmoset-model-details-content',
  imports: [PanelNavigationComponent, MarmosetModelDetailsBoxplotsSelectorComponent],
  templateUrl: './marmoset-model-details-content.component.html',
  styleUrls: ['./marmoset-model-details-content.component.scss'],
})
export class MarmosetModelDetailsContentComponent {
  model = input.required<MarmosetModel>();
  panels = input.required<Panel[]>();
  activePanel = input.required<string>();
  activeParent = input.required<string>();
  scrollToPanelNavElementOnInitialLoad = input.required<boolean>();
  panelChange = output<Panel>();

  plasmaBiomarkersWikiParams: SynapseWikiParams = { ownerId: 'syn66271427', wikiId: '641870' };
}

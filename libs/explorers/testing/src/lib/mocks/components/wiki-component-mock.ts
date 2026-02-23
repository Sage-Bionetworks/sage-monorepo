import { Component, input } from '@angular/core';
import { SynapseWikiParams } from '@sagebionetworks/explorers/models';

@Component({
  selector: 'explorers-wiki',
  standalone: true,
  template: '<div>wiki</div>',
})
export class MockWikiComponent {
  wikiParams = input.required<SynapseWikiParams>();
}

import { Component, input, viewChild } from '@angular/core';
import { SynapseWikiParams } from '@sagebionetworks/explorers/models';
import { ButtonModule } from 'primeng/button';
import { Popover, PopoverModule } from 'primeng/popover';
import { SvgIconComponent } from '../svg-icon/svg-icon.component';
import { WikiComponent } from '../wiki/wiki.component';

@Component({
  selector: 'explorers-popover-link',
  imports: [SvgIconComponent, ButtonModule, PopoverModule, WikiComponent],
  templateUrl: './popover-link.component.html',
  styleUrls: ['./popover-link.component.scss'],
})
export class PopoverLinkComponent {
  wikiParams = input.required<SynapseWikiParams>();

  isActive = false;

  readonly popover = viewChild.required<Popover>('popover');

  toggle(event: Event) {
    this.isActive = true;
    this.popover().toggle(event);
  }
}

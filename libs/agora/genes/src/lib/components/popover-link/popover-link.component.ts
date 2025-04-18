import { CommonModule } from '@angular/common';
import { Component, Input, ViewChild } from '@angular/core';
import { SvgIconComponent, WikiComponent } from '@sagebionetworks/agora/shared';
import { ButtonModule } from 'primeng/button';
import { Popover, PopoverModule } from 'primeng/popover';

@Component({
  selector: 'agora-popover-link',
  imports: [CommonModule, SvgIconComponent, ButtonModule, PopoverModule, WikiComponent],
  templateUrl: './popover-link.component.html',
  styleUrls: ['./popover-link.component.scss'],
})
export class PopoverLinkComponent {
  @Input() ownerId: string | undefined;
  @Input() wikiId: string | undefined;

  isActive = false;

  @ViewChild('popover') popover!: Popover;

  toggle(event: Event) {
    this.isActive = true;
    this.popover.toggle(event);
  }
}

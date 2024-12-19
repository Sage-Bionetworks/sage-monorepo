import { Component, Input, ViewChild } from '@angular/core';
import { OverlayPanel, OverlayPanelModule } from 'primeng/overlaypanel';
import { CommonModule } from '@angular/common';
import { SvgIconComponent, WikiComponent } from '@sagebionetworks/agora/shared';

@Component({
  selector: 'agora-overlay-panel-link',
  standalone: true,
  imports: [CommonModule, SvgIconComponent, OverlayPanelModule, WikiComponent],
  templateUrl: './overlay-panel-link.component.html',
  styleUrls: ['./overlay-panel-link.component.scss'],
})
export class OverlayPanelLinkComponent {
  @Input() icon = 'svg';
  @Input() text = '';

  @Input() ownerId: string | undefined;
  @Input() wikiId: string | undefined;

  isActive = false;
  hasActived = false;

  @ViewChild('panel') panel!: OverlayPanel;

  toggle(event: Event) {
    this.hasActived = true;
    this.isActive = !this.isActive;
    this.panel.toggle(event);
  }
}

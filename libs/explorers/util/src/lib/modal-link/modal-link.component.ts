import { CommonModule } from '@angular/common';
import { Component, input, ViewEncapsulation } from '@angular/core';
import { DialogModule } from 'primeng/dialog';
import { SvgIconComponent } from '../svg-icon/svg-icon.component';
import { WikiComponent } from '../wiki/wiki.component';
import { SynapseWikiParams } from '@sagebionetworks/explorers/models';

@Component({
  selector: 'explorers-modal-link',
  imports: [CommonModule, DialogModule, SvgIconComponent, WikiComponent],
  templateUrl: './modal-link.component.html',
  styleUrls: ['./modal-link.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class ModalLinkComponent {
  text = input('');
  textColor = input('');
  title = input('');
  wikiParams = input<SynapseWikiParams>();
  iconWidth = input(14);
  iconHeight = input(14);
  enableHoverEffects = input(true);
  isActive = false;
  hasActivated = false;

  toggle() {
    this.hasActivated = true;
    this.isActive = !this.isActive;
  }

  getTextColor() {
    return this.textColor() ? { color: this.textColor() } : {};
  }

  close() {
    this.isActive = false;
  }
}

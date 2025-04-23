import { CommonModule } from '@angular/common';
import { Component, Input, ViewEncapsulation } from '@angular/core';
import { DialogModule } from 'primeng/dialog';
import { SvgIconComponent } from '../svg-icon/svg-icon.component';
import { WikiComponent } from '../wiki/wiki.component';

@Component({
  selector: 'agora-modal-link',
  imports: [CommonModule, DialogModule, SvgIconComponent, WikiComponent],
  templateUrl: './modal-link.component.html',
  styleUrls: ['./modal-link.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class ModalLinkComponent {
  @Input() text = '';
  @Input() textColor = '';

  @Input() header = '';

  @Input() ownerId = '';
  @Input() wikiId = '';

  @Input() iconWidth = 14;
  @Input() iconHeight = 14;

  isActive = false;
  hasActivated = false;

  toggle() {
    this.hasActivated = true;
    this.isActive = !this.isActive;
  }

  getTextColor() {
    return this.textColor ? { color: this.textColor } : {};
  }
}

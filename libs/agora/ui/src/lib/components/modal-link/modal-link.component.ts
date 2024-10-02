import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { DialogModule } from 'primeng/dialog';
import { SvgIconComponent } from '../svg-icon/svg-icon.component';
import { WikiComponent } from 'libs/agora/wiki/src/lib/wiki.component';

@Component({
  selector: 'agora-modal-link',
  standalone: true,
  imports: [CommonModule, DialogModule, SvgIconComponent, WikiComponent],
  templateUrl: './modal-link.component.html',
  styleUrls: ['./modal-link.component.scss'],
})
export class ModalLinkComponent {
  @Input() icon = 'svg';
  @Input() text = '';
  @Input() header = '';

  @Input() ownerId = '';
  @Input() wikiId = '';

  isActive = false;
  hasActived = false;

  toggle() {
    this.hasActived = true;
    this.isActive = !this.isActive;
  }
}

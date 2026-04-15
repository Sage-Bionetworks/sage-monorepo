import { Component, input, model } from '@angular/core';
import { Dialog } from 'primeng/dialog';

@Component({
  selector: 'bixarena-modal-dialog',
  imports: [Dialog],
  templateUrl: './modal-dialog.component.html',
  styleUrl: './modal-dialog.component.scss',
})
export class ModalDialogComponent {
  readonly visible = model(false);
  readonly styleClass = input('');
  readonly dismissableMask = input(true);
  readonly closable = input(true);
}

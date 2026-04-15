import { Component, input, model, output } from '@angular/core';
import { Dialog } from 'primeng/dialog';

@Component({
  selector: 'bixarena-modal-dialog',
  imports: [Dialog],
  templateUrl: './modal-dialog.component.html',
  styleUrl: './modal-dialog.component.scss',
})
export class ModalDialogComponent {
  readonly visible = model(false);
  readonly heading = input('');
  readonly styleClass = input('');
  readonly dismissableMask = input(true);
  readonly closable = input(true);
  readonly closed = output<void>();

  onClose(): void {
    this.visible.set(false);
    this.closed.emit();
  }
}

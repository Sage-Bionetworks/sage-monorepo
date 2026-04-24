import { Component, model, output } from '@angular/core';
import { ButtonModule } from 'primeng/button';
import { ModalDialogComponent } from '../modal-dialog/modal-dialog.component';

@Component({
  selector: 'bixarena-login-modal',
  imports: [ModalDialogComponent, ButtonModule],
  templateUrl: './login-modal.component.html',
})
export class LoginModalComponent {
  readonly visible = model(false);
  readonly signIn = output<void>();
  readonly dismissed = output<void>();
}

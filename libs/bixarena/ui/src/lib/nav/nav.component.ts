import { Component, inject } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { AuthService, ThemeService } from '@sagebionetworks/bixarena/services';

@Component({
  selector: 'bixarena-nav',
  imports: [RouterModule, ButtonModule],
  templateUrl: './nav.component.html',
  styleUrl: './nav.component.scss',
})
export class NavComponent {
  readonly authService = inject(AuthService);
  readonly themeService = inject(ThemeService);
}

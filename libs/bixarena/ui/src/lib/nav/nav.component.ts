import { Component, computed, inject } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { AuthService, ThemeService } from '@sagebionetworks/bixarena/services';
import { AvatarComponent } from '../avatar/avatar.component';

@Component({
  selector: 'bixarena-nav',
  imports: [RouterModule, ButtonModule, AvatarComponent],
  templateUrl: './nav.component.html',
  styleUrl: './nav.component.scss',
})
export class NavComponent {
  readonly authService = inject(AuthService);
  readonly themeService = inject(ThemeService);

  readonly displayUser = computed(() => {
    const user = this.authService.user();
    const cached = this.authService.cachedUser();
    return user ? { username: user.preferred_username ?? '', avatarUrl: user.avatar_url } : cached;
  });

  readonly showAvatar = computed(() => this.displayUser() !== null);
}

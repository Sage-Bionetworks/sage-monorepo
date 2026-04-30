import {
  Component,
  computed,
  DestroyRef,
  HostListener,
  inject,
  PLATFORM_ID,
  signal,
} from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { NavigationEnd, Router, RouterModule } from '@angular/router';
import { filter } from 'rxjs/operators';
import { ButtonModule } from 'primeng/button';
import { AuthService, ThemeService } from '@sagebionetworks/bixarena/services';
import { AvatarComponent } from '../avatar/avatar.component';

// Mirror of $md-breakpoint in shared-styles/_variables.scss. Kept as a
// runtime constant because TS can't reach into Sass at the moment; if the
// shared breakpoint ever changes, update both places.
const MD_BREAKPOINT_PX = 768;

@Component({
  selector: 'bixarena-nav',
  imports: [RouterModule, ButtonModule, AvatarComponent],
  templateUrl: './nav.component.html',
  styleUrl: './nav.component.scss',
})
export class NavComponent {
  readonly authService = inject(AuthService);
  readonly themeService = inject(ThemeService);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);
  private readonly isBrowser = isPlatformBrowser(inject(PLATFORM_ID));

  readonly displayUser = computed(() => {
    const user = this.authService.user();
    const cached = this.authService.cachedUser();
    return user ? { username: user.preferred_username ?? '', avatarUrl: user.avatar_url } : cached;
  });

  readonly showAvatar = computed(() => this.displayUser() !== null);
  readonly menuOpen = signal(false);

  constructor() {
    this.router.events
      .pipe(
        filter((e) => e instanceof NavigationEnd),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe(() => this.closeMenu());
  }

  toggleMenu(): void {
    this.menuOpen.update((open) => !open);
    this.applyBodyScrollLock();
  }

  closeMenu(): void {
    if (!this.menuOpen()) return;
    this.menuOpen.set(false);
    this.applyBodyScrollLock();
  }

  @HostListener('document:keydown.escape')
  onEscape(): void {
    this.closeMenu();
  }

  @HostListener('window:resize')
  onResize(): void {
    if (this.isBrowser && window.innerWidth >= MD_BREAKPOINT_PX) {
      this.closeMenu();
    }
  }

  private applyBodyScrollLock(): void {
    if (!this.isBrowser) return;
    document.body.style.overflow = this.menuOpen() ? 'hidden' : '';
  }
}

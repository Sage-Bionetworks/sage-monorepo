import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';
import { BattleGateService } from './battle-gate.service';

export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  if (auth.isAuthenticated()) return true;
  if (isPlatformBrowser(inject(PLATFORM_ID))) {
    inject(BattleGateService).showLoginModal.set(true);
  }
  return router.createUrlTree(['/']);
};

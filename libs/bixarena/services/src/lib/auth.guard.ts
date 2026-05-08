import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { CanActivateFn } from '@angular/router';
import { AuthService } from './auth.service';
import { BattleGateService } from './battle-gate.service';

export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  if (auth.isAuthenticated()) return true;
  if (isPlatformBrowser(inject(PLATFORM_ID))) {
    const gate = inject(BattleGateService);
    gate.setLoginEntryPoint('nav_battle_link');
    gate.showLoginModal.set(true);
  }
  return false;
};

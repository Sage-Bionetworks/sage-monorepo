import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class AuthService {
  readonly isAuthenticated = signal(false);

  login(): void {
    // TODO: implement OAuth flow in PR 2
  }

  logout(): void {
    this.isAuthenticated.set(false);
  }
}

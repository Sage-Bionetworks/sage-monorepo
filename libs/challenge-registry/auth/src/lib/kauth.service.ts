import { Injectable } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';

@Injectable({
  providedIn: 'root',
})
export class KAuthService {
  constructor(private keycloakService: KeycloakService) {}

  async logout(): Promise<void> {
    console.log('KauthService.logout()');
    return this.keycloakService.logout();
  }

  async isLoggedIn(): Promise<boolean> {
    return this.keycloakService.isLoggedIn();
  }
}

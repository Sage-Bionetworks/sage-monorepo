import { Injectable } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';
import { KeycloakProfile } from 'keycloak-js';
import { from, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class KAuthService {
  constructor(private keycloakService: KeycloakService) {}

  login(): Observable<void> {
    return from(this.keycloakService.login());
  }

  logout(): Observable<void> {
    return from(this.keycloakService.logout());
  }

  isLoggedIn(): Observable<boolean> {
    return from(this.keycloakService.isLoggedIn());
  }

  getUserProfile(): Observable<KeycloakProfile> {
    return from(this.keycloakService.loadUserProfile());
  }
}

import { KeycloakService } from 'keycloak-angular';
// import { of } from 'rxjs';
// import { switchMap } from 'rxjs/operators';
// import { fromPromise } from 'rxjs/internal-compatibility';
// import { AppConfig } from '@challenge-registry/web/config';
// import { ConfigInitService } from './config-init.service';

export function initializeKeycloakFactory(
  // config: AppConfig,
  keycloak: KeycloakService
) {
  return () =>
    keycloak.init({
      config: {
        url: 'http://localhost:8081',
        realm: 'test',
        clientId: 'test-client',
        // url: config['KEYCLOAK_URL'] + '/auth',
        // realm: config['KEYCLOAK_REALM'],
        // clientId: config['KEYCLOAK_CLIENT_ID'],
      },
      initOptions: {
        onLoad: 'check-sso',
        silentCheckSsoRedirectUri:
          window.location.origin + '/assets/silent-check-sso.html',
      },
    });
}

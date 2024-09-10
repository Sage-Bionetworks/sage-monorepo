import { KeycloakService } from 'keycloak-angular';
// import { of } from 'rxjs';
// import { switchMap } from 'rxjs/operators';
// import { fromPromise } from 'rxjs/internal-compatibility';
import { ConfigService } from '@sagebionetworks/openchallenges/config';
// import { ConfigInitService } from './config-init.service';
import { isPlatformBrowser } from '@angular/common';

export function initializeKeycloakFactory(
  configService: ConfigService,
  keycloak: KeycloakService,
  platformId: Record<string, unknown>,
) {
  return () => {
    if (isPlatformBrowser(platformId)) {
      keycloak.init({
        config: {
          url: 'http://localhost:8080',
          realm: 'test', // configService.config.keycloakRealm,
          clientId: 'test-client',
          // url: config['KEYCLOAK_URL'] + '/auth',
          // realm: config['KEYCLOAK_REALM'],
          // clientId: config['KEYCLOAK_CLIENT_ID'],
        },
        initOptions: {
          onLoad: 'check-sso',
          silentCheckSsoRedirectUri: 'http://localhost:4200' + '/assets/silent-check-sso.html',
          // window.location.origin + '/assets/silent-check-sso.html',
          flow: 'standard',
        },
        bearerExcludedUrls: [],
      });
    }
  };
}

import { provideServerRendering } from '@angular/ssr';
import { mergeApplicationConfig, ApplicationConfig } from '@angular/core';
import { APP_BASE_URL_PROVIDER_INDEX, appConfig } from './app.config';
import { provideClientHydration } from '@angular/platform-browser';

const serverConfig: ApplicationConfig = {
  providers: [provideServerRendering(), provideClientHydration()],
};

// The file server.ts defines a provider that specifies 'APP_BASE_URL' based on the request protocol
// and host. If this provider could be defined in serverConfig above, there would be no need to
// manually remove the provider that specifies 'APP_BASE_URL' from appConfig used for client-side
// rendering. Also removing based on an index should be avoided: I would have preferred to remove it
// based on a property value but couldn't.
appConfig.providers.splice(APP_BASE_URL_PROVIDER_INDEX, 1);

export const config = mergeApplicationConfig(appConfig, serverConfig);

import { provideServerRendering } from '@angular/ssr';
import { mergeApplicationConfig, ApplicationConfig } from '@angular/core';
import { appConfig } from './app.config';

const serverConfig: ApplicationConfig = {
  providers: [
    provideServerRendering(),
    // Note: The new YAML-based config system automatically handles file location
    // in both SSR and browser contexts, so no additional providers are needed here.
  ],
};

export const config = mergeApplicationConfig(appConfig, serverConfig);

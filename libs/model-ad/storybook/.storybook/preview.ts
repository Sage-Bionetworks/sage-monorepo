import { inject, provideAppInitializer } from '@angular/core';
import { provideAnimations } from '@angular/platform-browser/animations';
import { ModelAdPreset } from '@sagebionetworks/model-ad/themes';
import { applicationConfig, Preview } from '@storybook/angular';
import { PrimeNG } from 'primeng/config';

function provideTheme(): void {
  const config = inject(PrimeNG);
  config.theme.set({
    preset: ModelAdPreset,
    options: {
      darkModeSelector: 'none',
    },
  });
}

const preview: Preview = {
  decorators: [
    applicationConfig({
      providers: [provideAnimations(), provideAppInitializer(provideTheme)],
    }),
  ],
};

export default preview;

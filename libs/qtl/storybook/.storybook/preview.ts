import { inject, provideAppInitializer } from '@angular/core';
import { provideAnimations } from '@angular/platform-browser/animations';
import { QtlPreset } from '@sagebionetworks/qtl/themes';
import { applicationConfig, Preview } from '@storybook/angular';
import { PrimeNG } from 'primeng/config';

function provideTheme(): void {
  const config = inject(PrimeNG);
  config.theme.set({
    preset: QtlPreset,
    options: {
      darkModeSelector: false || 'none',
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

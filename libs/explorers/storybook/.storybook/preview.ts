// https://stackoverflow.com/a/79006622
import { inject, provideAppInitializer } from '@angular/core';
import { provideAnimations } from '@angular/platform-browser/animations';
import { BasePreset } from '@sagebionetworks/explorers/themes';
import { applicationConfig, Preview } from '@storybook/angular';
import { PrimeNG } from 'primeng/config';

function provideTheme(): void {
  const config = inject(PrimeNG);
  config.theme.set({
    preset: BasePreset,
    options: {
      darkModeSelector: false || 'none', // prevent user preferences from setting primeng dark mode
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

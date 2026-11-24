import { inject, provideAppInitializer } from '@angular/core';
import { provideAnimations } from '@angular/platform-browser/animations';
import { LOADING_ICON_COLORS } from '@sagebionetworks/explorers/constants';
import { QTL_LOADING_ICON_COLORS } from '@sagebionetworks/qtl/config';
import { QtlPreset } from '@sagebionetworks/qtl/themes';
import { applicationConfig, Preview } from '@storybook/angular';
import { PrimeNG } from 'primeng/config';

function provideTheme(): void {
  const config = inject(PrimeNG);
  config.theme.set({
    preset: QtlPreset,
    options: {
      darkModeSelector: 'none',
    },
  });
}

const preview: Preview = {
  decorators: [
    applicationConfig({
      providers: [
        provideAnimations(),
        provideAppInitializer(provideTheme),
        { provide: LOADING_ICON_COLORS, useValue: QTL_LOADING_ICON_COLORS },
      ],
    }),
  ],
};

export default preview;

// https://stackoverflow.com/a/79006622
import { inject, provideAppInitializer } from '@angular/core';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideExplorersConfig } from '@sagebionetworks/explorers/services';
import { mockVisualizationOverviewPanes } from '@sagebionetworks/explorers/testing';
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
      providers: [
        provideAnimations(),
        provideAppInitializer(provideTheme),
        provideExplorersConfig({
          visualizationOverviewPanes: mockVisualizationOverviewPanes,
        }),
      ],
    }),
  ],
};

export default preview;

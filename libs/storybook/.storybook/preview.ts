// https://stackoverflow.com/a/79006622
import { inject, provideAppInitializer } from '@angular/core';
import { provideAnimations } from '@angular/platform-browser/animations';
import { AgoraPreset } from '@sagebionetworks/agora/themes';
import { BasePreset } from '@sagebionetworks/explorers/themes';
import { ModelAdPreset } from '@sagebionetworks/model-ad/themes';
import { applicationConfig, Preview } from '@storybook/angular';
import { PrimeNG } from 'primeng/config';

function provideTheme(): void {
  const config = inject(PrimeNG);

  // Read theme from Storybook toolbar
  const selectedTheme = (window as any).__STORYBOOK_PREVIEW__?.globals?.theme || 'explorers';

  let preset;
  switch (selectedTheme) {
    case 'agora':
      preset = AgoraPreset;
      break;
    case 'model-ad':
      preset = ModelAdPreset;
      break;
    case 'explorers':
    default:
      preset = BasePreset;
      break;
  }

  config.theme.set({
    preset,
    options: {
      darkModeSelector: false || 'none', // prevent user preferences from setting primeng dark mode
    },
  });
}

const preview: Preview = {
  globalTypes: {
    theme: {
      description: 'Global theme for components',
      defaultValue: 'explorers',
      toolbar: {
        title: 'Theme',
        icon: 'paintbrush',
        items: [
          { value: 'explorers', title: 'Explorers' },
          { value: 'agora', title: 'Agora' },
          { value: 'model-ad', title: 'Model-AD' },
        ],
        dynamicTitle: true,
      },
    },
  },
  decorators: [
    applicationConfig({
      providers: [provideAnimations(), provideAppInitializer(provideTheme)],
    }),
  ],
};

export default preview;

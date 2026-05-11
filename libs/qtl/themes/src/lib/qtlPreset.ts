import { definePreset } from '@primeuix/themes';
import { BasePreset } from '@sagebionetworks/explorers/themes';

export const QtlPreset = definePreset(BasePreset, {
  semantic: {
    primary: {
      500: '#2c5182',
    },
  },
  components: {
    button: {
      root: {
        roundedBorderRadius: '40px',
      },
    },
    tooltip: {
      root: {
        borderRadius: '0',
        shadow: '0 8px 24px rgba(53, 58, 63, 0.15)',
      },
      colorScheme: {
        light: {
          root: {
            background: 'white',
            color: '#22252A',
          },
        },
      },
    },
  },
});

import { definePreset } from '@primeuix/themes';
import { BasePreset } from '@sagebionetworks/explorers/themes';

export const QtlPreset = definePreset(BasePreset, {
  semantic: {
    primary: {
      50: '#f4f6f8',
      100: '#b9c5d5',
      200: '#95a8c0',
      300: '#7089aa',
      400: '#4e6e96',
      500: '#2c5182',
      600: '#254570',
      700: '#1f395b',
      800: '#182d48',
      900: '#122034',
      950: '#0b1421',
    },
  },
  components: {
    button: {
      root: {
        roundedBorderRadius: '40px',
      },
      colorScheme: {
        light: {
          root: {
            primary: {
              hoverBackground: '{primary.400}',
              hoverBorderColor: '{primary.400}',
              activeBackground: '{primary.300}',
              activeBorderColor: '{primary.300}',
            },
          },
        },
      },
    },
    dialog: {
      root: {
        borderRadius: '3px',
      },
      header: {
        padding: '32px 32px 24px',
      },
      content: {
        padding: '0 32px 32px',
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

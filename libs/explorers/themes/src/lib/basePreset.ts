import { definePreset } from '@primeuix/themes';
import Lara from '@primeuix/themes/lara';

export const BasePreset = definePreset(Lara, {
  semantic: {
    primary: {
      50: '#f6f9fb',
      100: '#d5e1ea',
      200: '#b4c9d9',
      300: '#93b1c8',
      400: '#7199b8',
      500: '#5081a7',
      600: '#446e8e',
      700: '#385a75',
      800: '#2c475c',
      900: '#203443',
      950: '#14202a',
    },
    mask: {
      transitionDuration: '0.15s',
    },
    colorScheme: {
      light: {
        surface: {
          0: '#ffffff',
          50: '#f8fafc',
          100: '#f1f5f9',
          200: '#e2e8f0',
          300: '#cbd5e1',
          400: '#94a3b8',
          500: '#64748b',
          600: '#475569',
          700: '#334155',
          800: '#1e293b',
          900: '#0f172a',
          950: '#020617',
        },
      },
      dark: {
        surface: {
          0: '#ffffff',
          50: '#fafafa',
          100: '#f4f4f5',
          200: '#e4e4e7',
          300: '#d4d4d8',
          400: '#a1a1aa',
          500: '#71717a',
          600: '#52525b',
          700: '#3f3f46',
          800: '#27272a',
          900: '#18181b',
          950: '#09090b',
        },
      },
    },
  },
  components: {
    button: {
      root: {
        roundedBorderRadius: '15px',
        label: {
          fontWeight: 'normal',
        },
        sm: {
          paddingX: '1.25rem',
          paddingY: '0.4rem',
        },
      },
      colorScheme: {
        light: {
          outlined: {
            primary: {
              borderColor: '{primary.500}',
            },
          },
        },
      },
    },
    datatable: {
      headerCell: {
        selectedBackground: 'transparent',
        padding: '0.0625rem 0.0625rem',
      },
      colorScheme: {
        light: {
          header: {
            background: 'transparent',
          },
          headerCell: {
            background: 'transparent',
            hoverBackground: 'transparent',
          },
        },
      },
    },
    dialog: {
      header: {
        padding: '40px 40px 0 40px',
      },
    },
    multiselect: {
      overlay: {
        borderRadius: '0',
      },
      list: {
        padding: '0',
      },
    },
    paginator: {
      navButton: {
        selectedBackground: '{primary.700}',
        selectedColor: '{primary.contrast.color}',
      },
    },
    popover: {
      root: {
        borderRadius: '0',
      },
      content: {
        padding: '0',
      },
    },
    radiobutton: {
      root: {
        width: '16px',
        height: '16px',
      },
    },
    select: {
      root: {
        focusRing: {
          width: '0',
          style: 'none',
          color: 'transparent',
          offset: '0',
          shadow: 'none',
        },
      },
    },
    toggleswitch: {
      root: {
        width: '2.25rem',
        height: '1.25rem',
      },
      handle: {
        size: '0.85rem',
      },
    },
    tooltip: {
      colorScheme: {
        light: {
          root: {
            background: '#63676c',
            color: '#ffffff',
          },
        },
      },
    },
  },
});

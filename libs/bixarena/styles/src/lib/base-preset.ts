import { definePreset } from '@primeuix/themes';
import Lara from '@primeuix/themes/lara';

export const BixArenaPreset = definePreset(Lara, {
  primitive: {
    fontFamily: "'DM Sans Variable', 'DM Sans', system-ui, sans-serif",
    borderRadius: {
      none: '0',
      xs: '2px',
      sm: '4px',
      md: '8px',
      lg: '12px',
      xl: '16px',
    },
    teal: { 400: '#34d4b4' },
    blue: { 400: '#6cb4fc' },
    violet: { 400: '#b49cfa' },
  },
  semantic: {
    primary: {
      50: '#fff7ed',
      100: '#ffedd5',
      200: '#ffddb3',
      300: '#fdba74',
      400: '#fb923c',
      500: '#f97316',
      600: '#ea580c',
      700: '#c2410c',
      800: '#9a3412',
      900: '#7c2d12',
      950: '#6c2e12',
    },
    colorScheme: {
      light: {
        surface: {
          0: '#fdfcfb',
          50: '#f9f7f4',
          100: '#f4f0eb',
          200: '#e8e2db',
          300: '#d6cfc7',
          400: '#b3aca5',
          500: '#918a83',
          600: '#7c746f',
          700: '#3a3a40',
          800: '#24252d',
          900: '#1b1818',
          950: '#110f0e',
        },
      },
      dark: {
        surface: {
          0: '#1b1818',
          50: '#221f1d',
          100: '#2a2624',
          200: '#3d3835',
          300: '#524c48',
          400: '#6b6560',
          500: '#918a83',
          600: '#b3aca5',
          700: '#d8d2cc',
          800: '#f4f0eb',
          900: '#f9f7f4',
          950: '#fdfcfb',
        },
        text: {
          color: '{surface.950}',
          hoverColor: '{surface.950}',
          mutedColor: '{surface.400}',
          hoverMutedColor: '{surface.300}',
        },
      },
    },
  },
});

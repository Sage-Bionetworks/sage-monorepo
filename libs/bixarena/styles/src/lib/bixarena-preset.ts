import { definePreset } from '@primeuix/themes';
import Lara from '@primeuix/themes/lara';

export const BixArenaPreset = definePreset(Lara, {
  primitive: {
    fontFamily: "'DM Sans Variable', 'DM Sans', system-ui, sans-serif",
  },
  semantic: {
    // Orange primary matches the live Gradio app (Gradio default theme primary)
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
        bixarena: {
          accent: '#14b8a6',
        },
      },
      dark: {
        surface: {
          0: '#09090b',
          50: '#18181b',
          100: '#27272a',
          200: '#3f3f46',
          300: '#52525b',
          400: '#71717a',
          500: '#a1a1aa',
          600: '#d4d4d8',
          700: '#e4e4e7',
          800: '#f4f4f5',
          900: '#fafafa',
          950: '#ffffff',
        },
        text: {
          color: '{surface.950}',
          hoverColor: '{surface.950}',
          mutedColor: '{surface.400}',
          hoverMutedColor: '{surface.300}',
        },
        bixarena: {
          accent: '#14b8a6',
        },
      },
    },
  },
});

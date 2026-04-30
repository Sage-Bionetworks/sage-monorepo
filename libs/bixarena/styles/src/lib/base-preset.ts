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
      50: '#ffedd5',
      100: '#ffddb3',
      200: '#fdba74',
      300: '#fb923c',
      400: '#f97316',
      500: '#ea580c',
      600: '#c2410c',
      700: '#9a3412',
      800: '#7c2d12',
      900: '#6c2e12',
      950: '#431407',
    },
    colorScheme: {
      light: {
        primary: {
          color: '{primary.300}',
          contrastColor: '#ffffff',
          hoverColor: '{primary.400}',
          activeColor: '{primary.500}',
        },
        teal: { 400: '#4ab8a3' },
        surface: {
          0: '#fbf7ef',
          50: '#f6f2ec',
          100: '#ffffff',
          200: '#ece7e0',
          300: '#dad4cb',
          400: '#c6bab3',
          500: '#9f9792',
          600: '#7c746f',
          700: '#3d3737',
          800: '#292726',
          900: '#1b1818',
          950: '#1a1714',
        },
        text: {
          color: '{surface.950}',
          hoverColor: '{surface.950}',
          mutedColor: '{surface.600}',
          hoverMutedColor: '{surface.700}',
        },
        content: {
          background: '{surface.0}',
          borderColor: '{surface.200}',
        },
        overlay: {
          select: { background: '{surface.0}', borderColor: '{surface.200}' },
          popover: { background: '{surface.0}', borderColor: '{surface.200}' },
          modal: { background: '{surface.0}', borderColor: '{surface.200}' },
        },
      },
      dark: {
        teal: { 400: '#34d4b4' },
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
          mutedColor: '{surface.600}',
          hoverMutedColor: '{surface.700}',
        },
        content: {
          background: '{surface.0}',
          borderColor: '{surface.200}',
        },
        overlay: {
          select: { background: '{surface.0}', borderColor: '{surface.200}' },
          popover: { background: '{surface.0}', borderColor: '{surface.200}' },
          modal: { background: '{surface.0}', borderColor: '{surface.200}' },
        },
      },
    },
  },
});

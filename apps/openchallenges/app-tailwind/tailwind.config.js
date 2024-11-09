const { createGlobPatternsForDependencies } = require('@nx/angular/tailwind');
const { join } = require('path');

/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    join(__dirname, 'src/**/!(*.stories|*.spec).{ts,html}'),
    ...createGlobPatternsForDependencies(__dirname),
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          DEFAULT: 'var(--color-dark-blue-600)',
          50: 'var(--color-dark-blue-50)',
          100: 'var(--color-dark-blue-100)',
          200: 'var(--color-dark-blue-200)',
          300: 'var(--color-dark-blue-300)',
          400: 'var(--color-dark-blue-400)',
          500: 'var(--color-dark-blue-500)',
          600: 'var(--color-dark-blue-600)',
          700: 'var(--color-dark-blue-700)',
          800: 'var(--color-dark-blue-800)',
          900: 'var(--color-dark-blue-900)',
        },
        accent: {
          DEFAULT: 'var(--color-light-purple-400)',
          50: 'var(--color-light-purple-50)',
          100: 'var(--color-light-purple-100)',
          200: 'var(--color-light-purple-200)',
          300: 'var(--color-light-purple-300)',
          400: 'var(--color-light-purple-400)',
          500: 'var(--color-light-purple-500)',
          600: 'var(--color-light-purple-600)',
          700: 'var(--color-light-purple-700)',
          800: 'var(--color-light-purple-800)',
          900: 'var(--color-light-purple-900)',
        },
        figma: {
          navbar: 'var(--figma-navbar)',
          accent1: 'var(--figma-accent1)',
          accent2: 'var(--figma-accent2)',
          primary1: 'var(--figma-primary1)',
          primary2: 'var(--figma-primary2)',
          navbardark: 'var(--figma-navbardark)',
          secondary1: 'var(--figma-secondary1)',
          secondary2: 'var(--figma-secondary2)',
          darkaccent1: 'var(--figma-darkaccent1)',
          darkaccent2: 'var(--figma-darkaccent2)',
          grayBlack: 'var(--figma-gray-black)',
          grayWhite: 'var(--figma-gray-white)',
          hoverLight: 'var(--figma-hover-light)',
          hoverDark: 'var(--figma-hover-dark)',
        },
      },
      fontFamily: {
        lato: ['var(--font-family-lato)'],
      },
      fontSize: {
        headline5: ['var(--font-headline-5)'],
        headline6: ['var(--font-headline-6)'],
        subtitle1: ['var(--font-subtitle-1)'],
        subtitle2: ['var(--font-subtitle-2)'],
        body1: ['var(--font-body-1)'],
        body2: ['var(--font-body-2)'],
        caption: ['var(--font-caption)'],
        button: ['var(--font-button)'],
      },
    },
  },
  plugins: [],
};

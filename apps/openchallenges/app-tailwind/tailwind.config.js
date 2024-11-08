const { createGlobPatternsForDependencies } = require('@nx/angular/tailwind');
const { join } = require('path');

/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    join(__dirname, 'src/**/!(*.stories|*.spec).{ts,html}'),
    ...createGlobPatternsForDependencies(__dirname),
  ],
  theme: {
    extend: {},
    colors: {
      primary: {
        light: 'green',
        DEFAULT: 'red',
        dark: 'blue',
      },
      secondary: {
        light: '#e9d5ff',
        DEFAULT: '#a855f7',
        dark: '#7e22ce',
      },
      white: '#ffffff',
      black: '#000000',
    },
    spacing: {
      sm: '1rem',
      md: '1.5rem',
      lg: '2rem',
      xl: '3rem',
    },
  },
  plugins: [],
};

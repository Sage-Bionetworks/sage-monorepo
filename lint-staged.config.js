// lint-staged.config.js

module.exports = {
  // Format JavaScript, TypeScript, and JSX/TSX files first, then lint
  '**/*.{js,jsx,ts,tsx}': (filenames) => [
    // Format files with Prettier first
    `prettier --write ${filenames.join(' ')}`,
    // Then run ESLint on the formatted files
    `nx affected --target=lint --files=${filenames.join(',')}`,
  ],

  // Format JSON, Markdown, and YAML files
  '**/*.{json,md,yaml,yml}': (filenames) => [`nx format:write --files=${filenames.join(',')}`],

  // Format Python files with Black first, then lint with Pylint
  '**/*.py': (filenames) => [
    // Format with Black
    `black ${filenames.join(' ')}`,
    // Lint with Pylint
    `pylint ${filenames.join(' ')}`,
  ],
};

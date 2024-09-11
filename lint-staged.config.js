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

  // Format Python files
  '**/*.py': (filenames) => [`black ${filenames.join(' ')}`],
};

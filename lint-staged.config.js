// lint-staged.config.js

module.exports = {
  // Run linting on all affected projects using Nx
  '*': (filenames) => [
    // Run Nx affected linting for all staged files
    `nx affected --target=lint --files=${filenames.join(',')}`,
  ],

  // Format JavaScript, TypeScript, and JSX/TSX files
  '**/*.{js,jsx,ts,tsx}': (filenames) => [
    // Format files with Prettier
    `prettier --write ${filenames.join(' ')}`,
  ],

  // Format JSON, Markdown, and YAML files
  '**/*.{json,md,yaml,yml}': (filenames) => [
    // Format files with Prettier
    `prettier --write ${filenames.join(' ')}`,
  ],

  // Format Python files with Black
  '**/*.py': (filenames) => [
    // Format with Black
    `black ${filenames.join(' ')}`,
  ],
};

// lint-staged.config.js

module.exports = {
  '**/*.{js,jsx,ts,tsx}': (filenames) => [
    // Format files with Prettier
    `prettier --write ${filenames.join(' ')}`,
    // Lint the projects affected by the staged files
    `nx affected --target=lint --files=${filenames.join(',')}`,
  ],

  '**/*.{json,md,yaml,yml}': (filenames) => [
    // Format files with Prettier
    `prettier --write ${filenames.join(' ')}`,
  ],

  '**/*.py': (filenames) => [
    // Format files with Black
    `poetry run black ${filenames.join(' ')}`,
    // Lint the projects affected by the staged files
    `nx affected --target=lint --files=${filenames.join(',')}`,
  ],

  '**/*[dD]ockerfile*': (filenames) => [
    // Lint Dockerfiles with Hadolint
    `hadolint ${filenames.join(' ')}`,
  ],
};

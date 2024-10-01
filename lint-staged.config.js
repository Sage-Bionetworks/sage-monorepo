// lint-staged.config.js

module.exports = {
  '**/*.{js,jsx,ts,tsx}': (filenames) => [
    // Format files with Prettier
    `prettier --write ${filenames.join(' ')}`,
    // Lint the projects affected by the staged files
    `nx affected --target=lint --files=${filenames.join(',')}`,
  ],

  '**/*.{css,scss}': (filenames) => [
    // Format files with Prettier
    `prettier --write ${filenames.join(' ')}`,
    // Lint files with Stylelint
    `stylelint --allow-empty-input ${filenames.join(' ')}`,
  ],

  '**/*.{json,md,yaml,yml,html}': (filenames) => [
    // Format files with Prettier
    `prettier --write ${filenames.join(' ')}`,
  ],

  '**/*.py': (filenames) => [
    // Format files with Black
    `poetry run black ${filenames.join(' ')}`,
    // Lint the projects affected by the staged files
    `nx affected --target=lint --files=${filenames.join(',')}`,
    // Type check the projects affected by the staged files
    `nx affected --target=type-check --files=${filenames.join(',')}`,
  ],

  '**/*[dD]ockerfile*': (filenames) => [
    // Lint Dockerfiles with Hadolint
    `hadolint ${filenames.join(' ')}`,
  ],

  '**/*.sql': (filenames) => [
    // Format files with Prettier
    `prettier --write ${filenames.join(' ')}`,
    // Lint files with SQLFluff
    `poetry run sqlfluff lint ${filenames.join(' ')}`,
  ],
};

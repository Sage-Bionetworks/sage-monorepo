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
    // Format files with Ruff
    `uv run ruff format ${filenames.join(' ')}`,
    // Lint the projects affected by the staged files
    `nx affected --target=lint --files=${filenames.join(',')}`,
    // Type check the projects affected by the staged files
    `nx affected --target=type-check --files=${filenames.join(',')}`,
  ],

  '**/*.{kt,kts}': (filenames) => [
    // Format files with ktlint
    `ktlint --format ${filenames.join(' ')}`,
  ],

  '**/Dockerfile.*': (filenames) => [
    // Lint Dockerfiles with Hadolint
    `hadolint ${filenames.join(' ')}`,
  ],

  '**/*.sql': (filenames) => [
    // Format files with Prettier
    // `prettier --write ${filenames.join(' ')}`,
    // Lint files with SQLFluff (conflicts with SQL formatter)
    // `poetry run sqlfluff lint ${filenames.join(' ')}`,
  ],

  '**/*': (filenames) => [
    // Test the projects affected by the staged files. This task assumes that formatting files and
    // testing affected projects can be safely run in parallel.
    `nx affected --target=test --files=${filenames.join(',')}`,
  ],
};

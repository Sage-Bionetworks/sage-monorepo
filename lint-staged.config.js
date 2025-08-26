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
    // TODO: Consider moving to pre-push if longer than 5s
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

  // Note: Tests are intentionally removed from pre-commit to improve developer velocity.
  // Lightweight tests run in pre-push hook and CI pipeline instead.
};

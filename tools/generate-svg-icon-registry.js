#!/usr/bin/env node
const fs = require('node:fs');
const path = require('node:path');
const prettier = require('prettier');

const REPO_ROOT = path.resolve(__dirname, '..');
const ICONS_DIR = path.join(REPO_ROOT, 'libs', 'explorers', 'assets', 'icons');
const URL_PREFIX = 'explorers-assets/icons';
const OUTPUT_FILE = path.join(
  REPO_ROOT,
  'libs',
  'explorers',
  'services',
  'src',
  'lib',
  'svg-icon-registry.gen.ts',
);

async function generate() {
  if (!fs.existsSync(ICONS_DIR)) {
    console.error(`Icons directory not found: ${ICONS_DIR}`);
    process.exit(1);
  }

  const checkMode = process.argv.includes('--check');

  const files = fs
    .readdirSync(ICONS_DIR)
    .filter((file) => file.endsWith('.svg'))
    .sort();

  const entries = files.map((file) => {
    const content = fs.readFileSync(path.join(ICONS_DIR, file), 'utf-8').trim();
    return [`${URL_PREFIX}/${file}`, content];
  });

  const lines = [
    '// AUTO-GENERATED FILE — DO NOT EDIT.',
    '// Run `node tools/generate-svg-icon-registry.js` to regenerate after adding,',
    '// removing, or modifying SVGs in libs/explorers/assets/icons/.',
    '',
    'export const SVG_ICON_REGISTRY: Readonly<Record<string, string>> = Object.freeze({',
  ];

  for (const [key, content] of entries) {
    lines.push(`  ${JSON.stringify(key)}: ${JSON.stringify(content)},`);
  }

  lines.push('});');
  lines.push('');

  const prettierConfig = await prettier.resolveConfig(OUTPUT_FILE);
  const formatted = await prettier.format(lines.join('\n'), {
    ...prettierConfig,
    filepath: OUTPUT_FILE,
  });

  if (checkMode) {
    const existing = fs.existsSync(OUTPUT_FILE) ? fs.readFileSync(OUTPUT_FILE, 'utf-8') : '';
    if (existing !== formatted) {
      console.error(
        `${OUTPUT_FILE} is out of date. Run \`pnpm generate:svg-icon-registry\` and commit the result.`,
      );
      process.exit(1);
    }
    console.log(`${OUTPUT_FILE} is up to date (${entries.length} icons).`);
    return;
  }

  fs.writeFileSync(OUTPUT_FILE, formatted);
  console.log(`Wrote ${OUTPUT_FILE} (${entries.length} icons).`);
}

generate();

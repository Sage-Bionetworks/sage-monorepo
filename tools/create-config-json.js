#!/usr/bin/env node
/**
 * Generates config.json from config.json.template by substituting environment variables.
 *
 * Supports POSIX shell parameter expansion syntax: ${VAR:-default}
 * - If VAR is set, uses its value
 * - If VAR is unset or empty, uses the default value
 *
 * Usage: node tools/create-config-json.js [templatePath] [outputPath]
 * - When called without arguments, runs from the project root (e.g., apps/agora/app)
 * - When called with arguments, uses the provided paths directly
 */

const fs = require('fs');
const path = require('path');

const templatePath = process.argv[2] || path.join(process.cwd(), 'src/config/config.json.template');
const outputPath = process.argv[3] || path.join(process.cwd(), 'src/config/config.json');

if (!fs.existsSync(templatePath)) {
  console.error(`Template not found: ${templatePath}`);
  process.exit(1);
}

const template = fs.readFileSync(templatePath, 'utf8');

const result = template.replace(/\${([^}]+)}/g, (_, expr) => {
  const [varName, defaultVal] = expr.split(':-');
  return process.env[varName] || defaultVal || '';
});

fs.writeFileSync(outputPath, result);
console.log(`Created ${outputPath}`);

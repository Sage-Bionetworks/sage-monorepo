// Source: https://redocly.com/docs/cli/guides/replace-servers-url
import ReplaceServersURL from './decorators/replace-servers-url.mjs';

/** @type {import('@redocly/cli').DecoratorsConfig} */
const decorators = {
  oas3: {
    'replace-servers-url': ReplaceServersURL,
  },
};

export default function replaceServersUrlPlugin() {
  return {
    id: 'plugin',
    decorators,
  };
}

// Source: https://redocly.com/docs/cli/guides/replace-servers-url
import ReplaceServersURL from './decorators/replace-servers-url.mjs';
import InjectApiKeySecurity from './decorators/inject-api-key-security.mjs';

/** @type {import('@redocly/cli').DecoratorsConfig} */
const decorators = {
  oas3: {
  'replace-servers-url': ReplaceServersURL,
  'inject-api-key-security': InjectApiKeySecurity,
  },
};

export default function replaceServersUrlPlugin() {
  return {
    id: 'plugin',
    decorators,
  };
}

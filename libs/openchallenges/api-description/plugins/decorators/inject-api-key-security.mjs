/**
 * Redocly decorator to inject an API key security scheme (X-API-Key) alongside jwtBearer.
 *
 * Options (all optional):
 *  - schemeName: name for the api key security scheme (default: 'apiKey')
 *  - headerName: header carrying the key (default: 'X-API-Key')
 *  - description: custom description text
 *
 * Behavior:
 *  - Adds components.securitySchemes[schemeName] if missing.
 *  - Ensures root-level security includes OR alternatives for jwtBearer and apiKey.
 *  - For operations that declare security containing jwtBearer (and not explicitly empty),
 *    append an OR alternative with the api key if absent.
 *  - Leaves operations with security: [] untouched (public endpoints).
 */

/** @type {import('@redocly/cli').OasDecorator} */
export default function InjectApiKeySecurity(options = {}) {
  const schemeName = options.schemeName || 'apiKey';
  const headerName = options.headerName || 'X-API-Key';
  const description =
    options.description ||
    'API key issued by OpenChallenges. Provide via X-API-Key header. The API Gateway exchanges it for a JWT internally; alternatively send a Bearer JWT directly.';

  function ensureRootSecurity(root) {
    if (!root.components) root.components = {};
    if (!root.components.securitySchemes) root.components.securitySchemes = {};

    // Add the api key scheme if absent
    if (!root.components.securitySchemes[schemeName]) {
      root.components.securitySchemes[schemeName] = {
        type: 'apiKey',
        in: 'header',
        name: headerName,
        description,
      };
    }

    // Ensure OR semantics at root (array of objects)
    const rootSecurity = Array.isArray(root.security) ? root.security : [];
    const hasJwt = rootSecurity.some((obj) => Object.prototype.hasOwnProperty.call(obj, 'jwtBearer'));
    const hasApiKey = rootSecurity.some((obj) => Object.prototype.hasOwnProperty.call(obj, schemeName));

    const newSecurity = [...rootSecurity];
    if (!hasJwt) {
      // Only add jwtBearer ref if it exists in securitySchemes (avoid dangling ref)
      if (root.components.securitySchemes.jwtBearer) {
        newSecurity.push({ jwtBearer: [] });
      }
    }
    if (!hasApiKey) {
      newSecurity.push({ [schemeName]: [] });
    }
    if (newSecurity.length > 0) {
      root.security = newSecurity;
    }
  }

  function enhanceOperation(op) {
    if (!op || typeof op !== 'object') return;
    if (!Object.prototype.hasOwnProperty.call(op, 'security')) {
      // Inherit from root; nothing to do.
      return;
    }
    if (Array.isArray(op.security)) {
      // security: [] means explicitly no auth -> leave it.
      if (op.security.length === 0) return;
      const hasApiKey = op.security.some((obj) => Object.prototype.hasOwnProperty.call(obj, schemeName));
      const hasJwt = op.security.some((obj) => Object.prototype.hasOwnProperty.call(obj, 'jwtBearer'));
      if (hasJwt && !hasApiKey) {
        op.security.push({ [schemeName]: [] });
      }
    }
  }

  return {
    Root: {
      leave(root) {
        ensureRootSecurity(root);
        if (root.paths && typeof root.paths === 'object') {
          for (const pathKey of Object.keys(root.paths)) {
            const pathItem = root.paths[pathKey];
            if (!pathItem || typeof pathItem !== 'object') continue;
            // HTTP methods in OAS
            for (const method of [
              'get',
              'put',
              'post',
              'delete',
              'patch',
              'options',
              'head',
              'trace',
            ]) {
              if (pathItem[method]) enhanceOperation(pathItem[method]);
            }
          }
        }
      },
    },
  };
}

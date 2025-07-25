// Source: https://redocly.com/docs/cli/guides/replace-servers-url
/** @type {import('@redocly/cli').OasDecorator} */
export default function ReplaceServersURL({ serverUrl }) {
  return {
    Server: {
      leave(Server) {
        if (serverUrl) {
          Server.url = serverUrl;
        }
      },
    },
  };
}

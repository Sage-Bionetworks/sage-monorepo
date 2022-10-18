import { ApplicationFunctionOptions, Probot, run } from 'probot';
// import * as express from 'express';

export const challengeBot = async (
  app: Probot,
  { getRouter }: ApplicationFunctionOptions
): Promise<void> => {
  if (!getRouter) {
    // throw new Error('getRouter is undefined');
    console.log('getRouter is undefined');
  }

  // if (getRouter) {
  // const router = getRouter('/api');

  // router.use(express.json());

  // router.get('/hello-world', (req, res) => {
  //   res.json({ a: 1 });
  // });
  // }

  // if (!getRouter) {
  //   throw new Error('getRouter is undefined');
  // }

  app.on('issues.opened', async (context) => {
    const issueComment = context.issue({
      body: 'Thanks for opening this issue!',
    });
    await context.octokit.issues.createComment(issueComment);
  });
  // For more information on building apps:
  // https://probot.github.io/docs/

  // To get your app running against GitHub, see:
  // https://probot.github.io/docs/development/
};

run(challengeBot);

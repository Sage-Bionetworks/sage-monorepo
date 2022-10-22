import { Context } from 'probot';
import pino from 'pino';
import { version } from '../package.json';

export const logger = pino();

export class ChallengeApp {
  private context: Context<'issues'>;
  private logger: pino.Logger;

  constructor(context: Context<'issues'>) {
    this.context = context;
    this.logger = logger.child({
      version,
      // repo: this.repo,
      // owner: this.owner,
      // pull_number: this.pullNumber,
      // sha: this.headSha,
    });
  }

  async run(): Promise<void> {
    const issueComment = this.context.issue({
      body: 'Thanks for opening this issue!',
    });
    await this.context.octokit.issues.createComment(issueComment);
  }

  // get repo(): string {
  //   return this.context.payload.repository.name;
  // }

  // get owner(): string {
  //   return this.context.payload.repository.owner.login;
  // }
}

// import { ApplicationFunctionOptions, Probot, run } from 'probot';
// import * as express from 'express';

// export const challengeBot = async (
//   app: Probot,
//   { getRouter }: ApplicationFunctionOptions
// ): Promise<void> => {
//   // if (!getRouter) {
//   //   throw new Error('getRouter is undefined');
//   //   // console.log('getRouter is undefined');
//   // }

//   // // if (getRouter) {
//   // const router = getRouter('/api');

//   // router.use(express.json());

//   // router.get('/hello-world', (req, res) => {
//   //   res.json({ a: 1 });
//   // });
//   // }

//   // if (!getRouter) {
//   //   throw new Error('getRouter is undefined');
//   // }

//   app.on('issues.opened', async (context) => {
//     const issueComment = context.issue({
//       body: 'Thanks for opening this issue!',
//     });
//     await context.octokit.issues.createComment(issueComment);
//   });
//   // For more information on building apps:
//   // https://probot.github.io/docs/

//   // To get your app running against GitHub, see:
//   // https://probot.github.io/docs/development/
// };

// run(challengeBot);

import { Context, Probot } from 'probot';
import { ChallengeApp } from './challenge-app';

export const buildChallengeApp = async (app: Probot) => {
  app.on('issues.opened', run);
  // app.on('pull_request.opened', processPullRequestEvent);

  async function run(context: Context<'issues'>) {
    const challengeApp = new ChallengeApp(context);
    await challengeApp.run();
  }

  // async function processPullRequestEvent(context: Context<'pull_request'>) {
  //   const challengeBot = new ChallengeBot(context);
  //   await challengeBot.run();
  // }
};

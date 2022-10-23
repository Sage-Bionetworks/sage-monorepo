import express, { Application, Router } from 'express';

export class Server {
  public expressApp: Application;

  constructor() {
    this.expressApp = express();
    this.expressApp.disable('x-powered-by');

    const router = Router();
    this.expressApp.use('/challenge-bot', router);
    this.expressApp.use(express.json());

    // this.expressApp.get('/challenge-bot/ping', (req, res) => {
    //   res.json({ pong: true });
    // });

    router.get('/ping', (req, res) => {
      res.json({ pong: true });
    });
  }
}

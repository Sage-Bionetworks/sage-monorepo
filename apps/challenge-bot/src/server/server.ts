import express, { Application } from 'express';

export class Server {
  public expressApp: Application;

  constructor() {
    this.expressApp = express();

    this.expressApp.get('/ping', (req, res) => {
      res.send('pong');
    });
  }
}

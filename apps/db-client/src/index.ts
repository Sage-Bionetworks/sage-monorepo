import { App } from './app';

const app = new App();
app.run();

process.on('SIGINT', () => {
  app.gracefulShutdown('app termination', () => {
    process.exit(0);
  });
});

process.on('SIGTERM', () => {
  app.gracefulShutdown('app termination', () => {
    process.exit(0);
  });
});

// For nodemon restarts
process.once('SIGUSR2', () => {
  app.gracefulShutdown('nodemon restart', () => {
    process.kill(process.pid, 'SIGUSR2');
  });
});

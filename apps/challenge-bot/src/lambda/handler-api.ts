import serverless from 'serverless-http';
import express from 'express';

const app = express();

app.get('/', (req, res) => {
  res.send('pong');
});

module.exports.api = serverless(app);

// const serverless = require('serverless-http');
// const Koa = require('koa'); // or any supported framework

// const app = new Koa();

// app.use(/* register your middleware as normal */);

// // this is it!
// module.exports.handler = serverless(app);

// // or as a promise
// const handler = serverless(app);
// module.exports.handler = async (event, context) => {
//   // you can do other things here
//   const result = await handler(event, context);
//   // and here
//   return result;
// };

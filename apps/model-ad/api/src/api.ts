import express from 'express';
import mongoose from 'mongoose';
import {
  comparisonToolConfigRoute,
  dataVersionRoute,
  modelOverviewRoute,
  modelRoute,
} from './components';

const mongodbUser = process.env.MONGODB_USER;
const mongodbPass = process.env.MONGODB_PASS;
const mongodbHost = process.env.MONGODB_HOST;
const mongodbPort = process.env.MONGODB_PORT;
const mongodbName = process.env.MONGODB_NAME;

if (!mongodbUser) {
  console.error('No MONGODB_USER environment variable has been defined.');
  process.exit(1);
}

if (!mongodbPass) {
  console.error('No MONGODB_PASS environment variable has been defined.');
  process.exit(1);
}

if (!mongodbHost) {
  console.error('No MONGODB_HOST environment variable has been defined.');
  process.exit(1);
}

if (!mongodbPort) {
  console.error('No MONGODB_PORT environment variable has been defined.');
  process.exit(1);
}

if (!mongodbName) {
  console.error('No MONGODB_NAME environment variable has been defined.');
  process.exit(1);
}

const mongoUri = `mongodb://${mongodbUser}:${mongodbPass}@${mongodbHost}:${mongodbPort}/${mongodbName}?authSource=admin`;

mongoose
  .connect(mongoUri)
  .then(() => console.log('Connected to MongoDB'))
  .catch((err) => console.error('Error connecting to MongoDB:', err));

mongoose.connection.on('error', console.error.bind(console, 'MongoDB connection error:'));

const router = express.Router();
mongoose.connection.once('open', async () => {
  router.get('/dataversion', dataVersionRoute);
  router.get('/models/:model', modelRoute);
  router.get('/comparison-tool-config', comparisonToolConfigRoute);
  router.get('/comparison-tools/model-overview', modelOverviewRoute);
});

export default router;

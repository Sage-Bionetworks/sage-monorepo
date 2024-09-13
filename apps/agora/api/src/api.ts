import express from 'express';
import mongoose from 'mongoose';
import { dataVersionRoute } from './components/dataversion';
import { teamsRoute } from './components/teams';

const mongoUri = process.env.MONGODB_URI;

if (!mongoUri) {
  console.error('No MONGODB_URI environment variable has been defined.');
  process.exit(1);
}

mongoose
  .connect(mongoUri)
  .then(() => console.log('Connected to MongoDB'))
  .catch((err) => console.error('Error connecting to MongoDB:', err));

mongoose.connection.on('error', console.error.bind(console, 'MongoDB connection error:'));

const router = express.Router();
mongoose.connection.once('open', async () => {
  router.get('/dataversion', dataVersionRoute);
  router.get('/teams', teamsRoute);
});

export default router;

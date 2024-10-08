import express from 'express';
import mongoose from 'mongoose';
import {
  allBiodomainsRoute,
  biodomainsRoute,
  comparisonGenesRoute,
  dataVersionRoute,
  distributionRoute,
  geneRoute,
  genesRoute,
  nominatedGenesRoute,
  searchGeneRoute,
  teamMemberImageRoute,
  teamsRoute,
} from './components';

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
  router.get('/biodomains', allBiodomainsRoute);
  router.get('/biodomains/:id', biodomainsRoute);
  router.get('/dataversion', dataVersionRoute);
  router.get('/distribution', distributionRoute);
  router.get('/genes/search', searchGeneRoute);
  router.get('/genes/comparison', comparisonGenesRoute);
  router.get('/genes/nominated', nominatedGenesRoute);
  router.get('/genes/:id', geneRoute);
  router.get('/genes', genesRoute);
  router.get('/teams', teamsRoute);
  router.get('/teamMembers/:name/image', teamMemberImageRoute);
});

export default router;

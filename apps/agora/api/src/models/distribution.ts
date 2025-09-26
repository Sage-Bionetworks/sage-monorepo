// -------------------------------------------------------------------------- //
// External
// -------------------------------------------------------------------------- //
import { Schema, model } from 'mongoose';

// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import {
  RnaDistribution,
  OverallScoresDistribution,
  ProteomicsDistribution,
} from '@sagebionetworks/agora/api-client';

// -------------------------------------------------------------------------- //
// Schemas
// -------------------------------------------------------------------------- //
const RnaDistributionSchema = new Schema<RnaDistribution>(
  {
    _id: { type: String, required: true },
    model: { type: String, required: true },
    tissue: { type: String, required: true },
    min: { type: Number, required: true },
    max: { type: Number, required: true },
    first_quartile: { type: Number, required: true },
    median: { type: Number, required: true },
    third_quartile: { type: Number, required: true },
  },
  { collection: 'rnaboxdistribution' },
);

const ProteomicDistributionSchema = new Schema<ProteomicsDistribution>(
  {
    type: String,
  },
  { collection: 'proteomicsboxdistribution' },
);

const OverallScoresDistributionSchema = new Schema<OverallScoresDistribution>(
  {
    distribution: [{ type: Number, required: true }],
    bins: [[{ type: Number, required: true }]],
    name: { type: String, required: true },
    syn_id: { type: String, required: true },
    wiki_id: { type: String, required: true },
  },
  { collection: 'genescoredistribution' },
);

// -------------------------------------------------------------------------- //
// Models
// -------------------------------------------------------------------------- //
export const RnaDistributionCollection = model<RnaDistribution>(
  'RnaDistributionCollection',
  RnaDistributionSchema,
);

export const ProteomicDistributionCollection = model<ProteomicsDistribution>(
  'ProteomicDistributionCollection',
  ProteomicDistributionSchema,
);

export const OverallScoresDistributionCollection = model<OverallScoresDistribution>(
  'OverallScoresDistributionCollection',
  OverallScoresDistributionSchema,
);

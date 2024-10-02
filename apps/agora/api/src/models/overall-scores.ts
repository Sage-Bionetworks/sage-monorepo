// -------------------------------------------------------------------------- //
// External
// -------------------------------------------------------------------------- //
import { Schema, model } from 'mongoose';

// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import { OverallScores } from '../../app/models';
export { OverallScores, OverallScoresDistribution } from '../../app/models';

// -------------------------------------------------------------------------- //
// Schemas
// -------------------------------------------------------------------------- //
const OverallScoresSchema = new Schema<OverallScores>(
  {
    ensembl_gene_id: { type: String, required: true },
    target_risk_score: { type: Number, required: true },
    genetics_score: { type: Number, required: true },
    multi_omics_score: { type: Number, required: true },
    literature_score: { type: Number, required: true },
  },
  { collection: 'genesoverallscores' },
);

// -------------------------------------------------------------------------- //
// Models
// -------------------------------------------------------------------------- //
export const OverallScoresCollection = model<OverallScores>(
  'OverallScoresCollection',
  OverallScoresSchema,
);

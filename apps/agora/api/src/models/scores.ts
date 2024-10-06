// -------------------------------------------------------------------------- //
// External
// -------------------------------------------------------------------------- //
import { Schema, model } from 'mongoose';

// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import { Scores } from 'libs/agora/models';

// -------------------------------------------------------------------------- //
// Schemas
// -------------------------------------------------------------------------- //
const ScoresSchema = new Schema<Scores>(
  {
    ensembl_gene_id: { type: String, required: true },
    target_risk_score: { type: Number, required: true },
    genetics_score: { type: Number, required: true },
    multi_omics_score: { type: Number, required: true },
  },
  { collection: 'genesoverallscores' },
);

// -------------------------------------------------------------------------- //
// Models
// -------------------------------------------------------------------------- //
export const ScoresCollection = model<Scores>('ScoresCollection', ScoresSchema);

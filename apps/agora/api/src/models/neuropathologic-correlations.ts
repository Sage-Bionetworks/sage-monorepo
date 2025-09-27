// -------------------------------------------------------------------------- //
// External
// -------------------------------------------------------------------------- //
import { Schema, model } from 'mongoose';

// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import { NeuropathologicCorrelation } from '@sagebionetworks/agora/api-client';

// -------------------------------------------------------------------------- //
// Schemas
// -------------------------------------------------------------------------- //
const NeuropathologicCorrelationSchema = new Schema<NeuropathologicCorrelation>(
  {
    _id: { type: String, required: true },
    ensg: { type: String, required: true },
    gname: { type: String, required: true },
    oddsratio: { type: Number, required: true },
    ci_lower: { type: Number, required: true },
    ci_upper: { type: Number, required: true },
    pval: { type: Number, required: true },
    pval_adj: { type: Number, required: true },
    neuropath_type: { type: String, required: true },
  },
  { collection: 'genesneuropathcorr' },
);

// -------------------------------------------------------------------------- //
// Models
// -------------------------------------------------------------------------- //
export const NeuropathologicCorrelationCollection = model<NeuropathologicCorrelation>(
  'NeuropathologicCorrelationCollection',
  NeuropathologicCorrelationSchema,
);

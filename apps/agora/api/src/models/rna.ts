// -------------------------------------------------------------------------- //
// External
// -------------------------------------------------------------------------- //
import { Schema, model } from 'mongoose';

// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import { RnaDifferentialExpression } from '@sagebionetworks/agora/api-client';

// -------------------------------------------------------------------------- //
// Schemas
// -------------------------------------------------------------------------- //
const RnaDifferentialExpressionSchema = new Schema<RnaDifferentialExpression>(
  {
    _id: { type: String, required: true },
    ensembl_gene_id: { type: String, required: true },
    hgnc_symbol: { type: String, required: true },
    logfc: { type: Number, required: true },
    fc: { type: Number, required: true },
    ci_l: { type: Number, required: true },
    ci_r: { type: Number, required: true },
    adj_p_val: { type: Number, required: true },
    tissue: { type: String, required: true },
    study: { type: String, required: true },
    model: { type: String, required: true },
  },
  { collection: 'genes' },
);

// -------------------------------------------------------------------------- //
// Models
// -------------------------------------------------------------------------- //
export const RnaDifferentialExpressionCollection = model<RnaDifferentialExpression>(
  'RnaDifferentialExpressionCollection',
  RnaDifferentialExpressionSchema,
);

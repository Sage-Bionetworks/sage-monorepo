// -------------------------------------------------------------------------- //
// External
// -------------------------------------------------------------------------- //
import { Schema, model } from 'mongoose';

// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import { Metabolomics } from '@sagebionetworks/agora/api-client';

// -------------------------------------------------------------------------- //
// Schemas
// -------------------------------------------------------------------------- //
const MetabolomicsSchema = new Schema<Metabolomics>(
  {
    _id: { type: String, required: true },
    associated_gene_name: { type: String, required: true },
    ensembl_gene_id: { type: String, required: true },
    metabolite_id: { type: String, required: true },
    metabolite_full_name: { type: String, required: true },
    association_p: { type: Number, required: true },
    gene_wide_p_threshold_1kgp: { type: Number, required: true },
    n_per_group: [{ type: Number, required: true }],
    boxplot_group_names: [{ type: String, required: true }],
    ad_diagnosis_p_value: [{ type: Number, required: true }],
    transposed_boxplot_stats: [[{ type: Number, required: true }]],
  },
  { collection: 'genesmetabolomics' },
);

// -------------------------------------------------------------------------- //
// Models
// -------------------------------------------------------------------------- //
export const MetabolomicsCollection = model<Metabolomics>(
  'MetabolomicsCollection',
  MetabolomicsSchema,
);

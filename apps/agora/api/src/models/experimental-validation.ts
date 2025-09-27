import { Schema, model } from 'mongoose';
import { ExperimentalValidation } from '@sagebionetworks/agora/api-client';

const ExperimentalValidationSchema = new Schema<ExperimentalValidation>(
  {
    _id: { type: String, required: true },
    ensembl_gene_id: { type: String, required: true },
    hgnc_symbol: { type: String, required: true },
    hypothesis_tested: { type: String, required: true },
    summary_findings: { type: String, required: true },
    published: { type: String, required: true },
    reference: { type: String, required: true },
    species: { type: String, required: true },
    model_system: { type: String, required: true },
    outcome_measure: { type: String, required: true },
    outcome_measure_details: { type: String, required: true },
    balanced_for_sex: { type: String, required: true },
    contributors: { type: String, required: true },
    team: { type: String, required: true },
    reference_doi: { type: String, required: true },
    date_report: { type: String, required: true },
  },
  { collection: 'geneexpvalidation' },
);

// -------------------------------------------------------------------------- //
// Models
// -------------------------------------------------------------------------- //
export const ExperimentalValidationCollection = model<ExperimentalValidation>(
  'ExperimentalValidationCollection',
  ExperimentalValidationSchema,
);

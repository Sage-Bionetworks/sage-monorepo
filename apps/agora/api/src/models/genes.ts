// -------------------------------------------------------------------------- //
// External
// -------------------------------------------------------------------------- //
import { Schema, model } from 'mongoose';

// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import {
  Gene,
  MedianExpression,
  TargetNomination,
  Druggability,
  EnsemblInfo,
} from '@sagebionetworks/agora/api-client';
export { Gene } from '@sagebionetworks/agora/api-client';

// -------------------------------------------------------------------------- //
// Schemas
// -------------------------------------------------------------------------- //
const TargetNominationSchema = new Schema<TargetNomination>({
  source: { type: String, required: true },
  team: { type: String, required: true },
  rank: { type: String, required: true },
  hgnc_symbol: { type: String, required: true },
  target_choice_justification: { type: String, required: true },
  predicted_therapeutic_direction: { type: String, required: true },
  data_used_to_support_target_selection: { type: String, required: true },
  data_synapseid: { type: String, required: true },
  study: { type: String, required: true },
  input_data: { type: String, required: true },
  validation_study_details: { type: String, required: true },
  initial_nomination: { type: Number, required: true },
});

const MedianExpressionSchema = new Schema<MedianExpression>({
  min: Number,
  first_quartile: Number,
  median: Number,
  mean: Number,
  third_quartile: Number,
  max: Number,
  tissue: { type: String, required: true },
});

const EnsemblInfoSchema = new Schema<EnsemblInfo>({
  ensembl_release: { type: Number, required: true },
  ensembl_possible_replacements: { type: [String], required: true },
  ensembl_permalink: { type: String, required: true },
});

const DruggabilitySchema = new Schema<Druggability>({
  sm_druggability_bucket: { type: Number, required: true },
  safety_bucket: { type: Number, required: true },
  abability_bucket: { type: Number, required: true },
  pharos_class: { type: [String], required: true },
  classification: { type: String, required: true },
  safety_bucket_definition: { type: String, required: true },
  abability_bucket_definition: { type: String, required: true },
});

const GeneSchema = new Schema<Gene>(
  {
    _id: { type: String, required: true },
    ensembl_gene_id: { type: String, required: true },
    name: { type: String, required: true },
    summary: { type: String, required: true },
    hgnc_symbol: { type: String, required: true },
    alias: [{ type: String, required: true }],
    uniprotkb_accessions: [{ type: String, required: true }],
    is_igap: { type: Boolean, required: true },
    is_eqtl: { type: Boolean, required: true },
    is_any_rna_changed_in_ad_brain: { type: Boolean, required: true },
    rna_brain_change_studied: { type: Boolean, required: true },
    is_any_protein_changed_in_ad_brain: { type: Boolean, required: true },
    protein_brain_change_studied: { type: Boolean, required: true },
    target_nominations: { type: [TargetNominationSchema], required: true },
    median_expression: { type: [MedianExpressionSchema], required: true },
    druggability: { type: DruggabilitySchema, required: true },
    total_nominations: { type: Number, required: true },
    ensembl_info: { type: EnsemblInfoSchema, required: true },
  },
  { collection: 'geneinfo' },
);

// -------------------------------------------------------------------------- //
// Models
// -------------------------------------------------------------------------- //
export const GeneCollection = model<Gene>('GeneCollection', GeneSchema);

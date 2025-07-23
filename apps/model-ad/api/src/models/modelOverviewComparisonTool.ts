import { ModelOverview, ModelOverviewLink } from '@sagebionetworks/model-ad/api-client-angular';
import { Schema, model } from 'mongoose';

const ModelOverviewLinkSchema = new Schema<ModelOverviewLink>({
  link_text: { type: String },
  link_url: { type: String },
});

const ModelOverviewSchema = new Schema<ModelOverview>(
  {
    model: { type: String, required: true },
    model_type: { type: String, required: true },
    matched_controls: { type: [String], required: true },
    gene_expression: { type: ModelOverviewLinkSchema, required: true },
    disease_correlation: { type: ModelOverviewLinkSchema, required: true },
    biomarkers: { type: ModelOverviewLinkSchema, required: true },
    pathology: { type: ModelOverviewLinkSchema, required: true },
    study_data: { type: ModelOverviewLinkSchema, required: true },
    jax_strain: { type: ModelOverviewLinkSchema, required: true },
    center: { type: ModelOverviewLinkSchema, required: true },
  },
  {
    collection: 'model_overview',
  },
);

export const ModelOverviewCollection = model<ModelOverview>(
  'ModelOverviewCollection',
  ModelOverviewSchema,
);

import { ModelOverview, ModelOverviewLink } from '@sagebionetworks/model-ad/api-client-angular';
import { Schema, model } from 'mongoose';

const ModelOverviewLinkSchema = new Schema<ModelOverviewLink>({
  link_text: { type: String },
  link_url: { type: String },
});

const ModelOverviewSchema = new Schema<ModelOverview>(
  {
    name: { type: String, required: true },
    model_type: { type: String, required: true },
    matched_controls: { type: String, required: true },
    gene_expression: { type: ModelOverviewLinkSchema, required: false },
    disease_correlation: { type: ModelOverviewLinkSchema, required: false },
    biomarkers: { type: ModelOverviewLinkSchema, required: false },
    pathology: { type: ModelOverviewLinkSchema, required: false },
    study_data: { type: ModelOverviewLinkSchema, required: true },
    jax_strain: { type: ModelOverviewLinkSchema, required: true },
    center: { type: ModelOverviewLinkSchema, required: true },
    modified_genes: { type: [String], required: true },
    available_data: {
      type: [String],
      required: true,
      enum: ModelOverview.AvailableDataEnum,
    },
  },
  {
    collection: 'model_overview',
  },
);

export const ModelOverviewCollection = model<ModelOverview>(
  'ModelOverviewCollection',
  ModelOverviewSchema,
);

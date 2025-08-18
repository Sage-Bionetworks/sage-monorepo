import {
  GeneticInfo,
  IndividualData,
  Model,
  ModelData,
} from '@sagebionetworks/model-ad/api-client-angular';
import { Schema, model } from 'mongoose';
export { Model } from '@sagebionetworks/model-ad/api-client-angular';

const IndividualDataSchema = new Schema<IndividualData>({
  genotype: { type: String, required: true },
  sex: { type: String, required: true, enum: ['Female', 'Male'] },
  individual_id: { type: String, required: true },
  value: { type: Number, required: true },
});

const ModelDataSchema = new Schema<ModelData>({
  name: { type: String, required: true },
  evidence_type: { type: String, required: true },
  tissue: { type: String, required: true },
  age: { type: String, required: true },
  units: { type: String, required: true },
  data: { type: [IndividualDataSchema], required: true },
});

const GeneticInfoSchema = new Schema<GeneticInfo>({
  modified_gene: { type: String, required: true },
  ensembl_gene_id: { type: String, required: true },
  allele: { type: String, required: true },
  allele_type: { type: String, required: true },
  mgi_allele_id: { type: Number, required: true },
});

const ModelSchema = new Schema<Model>(
  {
    name: { type: String, required: true },
    matched_controls: { type: [String], required: true },
    model_type: { type: String, required: true },
    contributing_group: { type: String, required: true },
    study_synid: { type: String, required: true },
    rrid: { type: String, required: true },
    jax_id: { type: Number, required: true },
    alzforum_id: { type: String, required: true },
    genotype: { type: String, required: true },
    aliases: { type: [String], required: true },
    genetic_info: { type: [GeneticInfoSchema], required: true },
    biomarkers: { type: [ModelDataSchema], required: true },
    pathology: { type: [ModelDataSchema], required: true },
  },
  { collection: 'model_details' },
);

export const ModelsCollection = model<Model>('ModelsCollection', ModelSchema);

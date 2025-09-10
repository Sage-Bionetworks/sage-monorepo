import {
  CorrelationResult,
  DiseaseCorrelation,
} from '@sagebionetworks/model-ad/api-client-angular';
import { Schema, model } from 'mongoose';

const CorrelationResultSchema = new Schema<CorrelationResult>({
  module: { type: String, required: true },
  correlation: { type: Number, required: true },
  adj_p_value: { type: Number, required: true },
});

const DiseaseCorrelationSchema = new Schema<DiseaseCorrelation>(
  {
    name: { type: String, required: true },
    matched_control: { type: String, required: true },
    model_type: { type: String, required: true },
    modified_genes: { type: [String], required: true },
    cluster: { type: String, required: true },
    age: { type: String, required: true },
    sex: { type: String, required: true },
    results: { type: [CorrelationResultSchema], required: true },
  },
  {
    collection: 'disease_correlation',
  },
);

export const DiseaseCorrelationCollection = model<DiseaseCorrelation>(
  'DiseaseCorrelationCollection',
  DiseaseCorrelationSchema,
);

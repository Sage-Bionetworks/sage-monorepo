import { CorrelationResult, DiseaseCorrelation } from '@sagebionetworks/model-ad/api-client';
import { Schema, model } from 'mongoose';

const CorrelationResultSchema = new Schema<CorrelationResult>({
  correlation: { type: Number, required: true },
  adj_p_val: { type: Number, required: true },
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
    IFG: { type: CorrelationResultSchema, required: false },
    PHG: { type: CorrelationResultSchema, required: false },
    TCX: { type: CorrelationResultSchema, required: false },
    CBE: { type: CorrelationResultSchema, required: false },
    DLPFC: { type: CorrelationResultSchema, required: false },
    FP: { type: CorrelationResultSchema, required: false },
    STG: { type: CorrelationResultSchema, required: false },
  },
  {
    collection: 'disease_correlation',
  },
);

export const DiseaseCorrelationCollection = model<DiseaseCorrelation>(
  'DiseaseCorrelationCollection',
  DiseaseCorrelationSchema,
);

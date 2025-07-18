import {
  ComparisonToolConfig,
  ComparisonToolConfigColumn,
} from '@sagebionetworks/model-ad/api-client-angular';
import { Schema, model } from 'mongoose';

const ComparisonToolConfigColumnSchema = new Schema<ComparisonToolConfigColumn>({
  name: { type: String, required: true },
  type: { type: String, required: true },
  column_type: { type: String, required: true },
  tooltip: { type: String, required: true },
  sort_tooltip: { type: String, required: true },
});

const ComparisonToolConfigSchema = new Schema<ComparisonToolConfig>(
  {
    page: { type: String, required: true },
    dropdowns: { type: [String], required: true },
    columns: {
      type: [ComparisonToolConfigColumnSchema],
      required: true,
    },
  },
  {
    collection: 'ui_config',
  },
);

export const ComparisonToolConfigCollection = model<ComparisonToolConfig>(
  'ComparisonToolConfigCollection',
  ComparisonToolConfigSchema,
);

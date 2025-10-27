import {
  ComparisonToolConfig,
  ComparisonToolConfigColumn,
  ComparisonToolConfigFilter,
} from '@sagebionetworks/model-ad/api-client';
import { Schema, model } from 'mongoose';

const ComparisonToolConfigFilterSchema = new Schema<ComparisonToolConfigFilter>({
  name: { type: String, required: true },
  data_key: { type: String, required: true },
  short_name: { type: String, required: false },
  values: { type: [String], required: true },
});

const ComparisonToolConfigColumnSchema = new Schema<ComparisonToolConfigColumn>({
  name: { type: String, required: false },
  type: { type: String, required: true },
  data_key: { type: String, required: true },
  tooltip: { type: String, required: false },
  sort_tooltip: { type: String, required: false },
  link_text: { type: String, required: false },
  link_url: { type: String, required: false },
});

const ComparisonToolConfigSchema = new Schema<ComparisonToolConfig>(
  {
    page: { type: String, required: true },
    dropdowns: { type: [String], required: true },
    row_count: { type: String, required: true },
    columns: {
      type: [ComparisonToolConfigColumnSchema],
      required: true,
    },
    filters: { type: [ComparisonToolConfigFilterSchema], required: true },
  },
  {
    collection: 'ui_config',
  },
);

export const ComparisonToolConfigCollection = model<ComparisonToolConfig>(
  'ComparisonToolConfigCollection',
  ComparisonToolConfigSchema,
);

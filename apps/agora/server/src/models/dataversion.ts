import { Schema, model } from 'mongoose';
import { DataVersion } from '../api-models';

const DataVersionSchema = new Schema<DataVersion>(
  {
    data_file: { type: String, required: true },
    data_version: { type: String, required: true },
    team_images_id: { type: String, required: true },
  },
  {
    collection: 'dataversion',
  }
);

export const DataVersionCollection = model<DataVersion>(
  'DataVersionCollection',
  DataVersionSchema
);

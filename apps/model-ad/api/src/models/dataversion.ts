import { Dataversion } from '@sagebionetworks/model-ad/api-client-angular';
import { Schema, model } from 'mongoose';

const DataVersionSchema = new Schema<Dataversion>(
  {
    data_file: { type: String, required: true },
    data_version: { type: String, required: true },
  },
  {
    collection: 'dataversion',
  },
);

export const DataVersionCollection = model<Dataversion>('DataVersionCollection', DataVersionSchema);

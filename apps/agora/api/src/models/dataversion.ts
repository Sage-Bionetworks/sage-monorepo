import { Schema, model } from 'mongoose';
import { Dataversion } from '@sagebionetworks/agora/api-client-angular';

const DataVersionSchema = new Schema<Dataversion>(
  {
    data_file: { type: String, required: true },
    data_version: { type: String, required: true },
    team_images_id: { type: String, required: true },
  },
  {
    collection: 'dataversion',
  },
);

export const DataVersionCollection = model<Dataversion>('DataVersionCollection', DataVersionSchema);

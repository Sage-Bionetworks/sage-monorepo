import { model, ObjectId, Schema } from 'mongoose';

export interface ChallengeReadme {
  _id?: ObjectId; // TODO make required after adding _id to JSON seed
  text: string;
  createdAt: Date;
  updatedAt: Date;
}

const options = {
  collection: 'challenge_readme',
  timestamps: true,
};

export const ChallengeReadmeSchema = new Schema<ChallengeReadme>(
  {
    _id: { type: Schema.Types.ObjectId, auto: true }, // TODO make required after adding _id to JSON seed
    text: { type: String, required: true },
  },
  options
);

export const ChallengeReadmeModel = model(
  'ChallengeReadme',
  ChallengeReadmeSchema
);

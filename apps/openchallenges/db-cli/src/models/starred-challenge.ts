import { Schema, model } from 'mongoose';

interface StarredChallenge {
  userId: string;
  challengeId: string;
}

const options = {
  collection: 'starred_challenge',
};

const StarredChallengeSchema = new Schema<StarredChallenge>(
  {
    userId: { type: String, required: true },
    challengeId: { type: String, required: true },
  },
  options
);

export const StarredChallengeModel = model(
  'StarredChallenge',
  StarredChallengeSchema
);

// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import { cache } from '../helpers';
import { ScoresCollection } from '../models';
import { Scores } from 'libs/agora/models';

// -------------------------------------------------------------------------- //
// Functions
// -------------------------------------------------------------------------- //

export async function getAllScores() {
  let scores: Scores[] | undefined = cache.get('scores');

  if (scores) {
    return scores;
  }

  scores = await ScoresCollection.find().lean().exec();

  cache.set('scores', scores);
  return scores;
}

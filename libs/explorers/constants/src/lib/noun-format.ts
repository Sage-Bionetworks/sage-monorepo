import { ComparisonToolNoun } from '@sagebionetworks/explorers/models';
import { capitalizeFirstLetter, pluralize } from '@sagebionetworks/shared/util';

export const labelCase = (word: string): string => capitalizeFirstLetter(word.toLowerCase());

export const sentenceCase = (word: string): string => word.toLowerCase();

export const nounForCount = (count: number, noun: ComparisonToolNoun): string =>
  pluralize(noun.singular, count, noun.plural);

export const countLabel = (count: number, noun: ComparisonToolNoun): string =>
  `${count} ${labelCase(nounForCount(count, noun))}`;

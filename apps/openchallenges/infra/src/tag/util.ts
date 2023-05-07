import { IConstruct } from 'constructs';
import { TaggableConstruct } from './taggable-construct';

export function isTaggableConstruct(x: IConstruct): x is TaggableConstruct {
  return 'tags' in x && 'tagsInput' in x;
}

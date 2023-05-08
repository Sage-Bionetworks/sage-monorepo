import { IConstruct } from 'constructs';

// Not all constructs are taggable, so we need to filter it
export type TaggableConstruct = IConstruct & {
  tags?: { [key: string]: string };
  tagsInput?: { [key: string]: string };
};

export function isTaggableConstruct(x: IConstruct): x is TaggableConstruct {
  return 'tags' in x && 'tagsInput' in x;
}

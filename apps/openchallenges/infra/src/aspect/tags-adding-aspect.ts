import { IAspect } from 'cdktf/lib/aspect';
import { IConstruct } from 'constructs/lib/construct';

// Not all constructs are taggable, so we need to filter it
type TaggableConstruct = IConstruct & {
  tags?: { [key: string]: string };
  tagsInput?: { [key: string]: string };
};

function isTaggableConstruct(x: IConstruct): x is TaggableConstruct {
  return 'tags' in x && 'tagsInput' in x;
}

export class TagsAddingAspect implements IAspect {
  constructor(private tagsToAdd: Record<string, string>) {}

  // This method is called on every Construct within the specified scope (resources, data sources,
  // etc.).
  visit(node: IConstruct) {
    if (isTaggableConstruct(node)) {
      // We need to take the input value to not create a circular reference
      const currentTags = node.tagsInput || {};
      node.tags = { ...this.tagsToAdd, ...currentTags };
    }
  }
}

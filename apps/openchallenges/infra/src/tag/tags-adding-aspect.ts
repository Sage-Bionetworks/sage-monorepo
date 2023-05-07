import { IAspect } from 'cdktf/lib/aspect';
import { IConstruct } from 'constructs/lib/construct';
import { isTaggableConstruct } from './util';
import { logger } from '../logger';

/** This aspect adds the tags specified to every Construct within the specified scope. */
export class TagsAddingAspect implements IAspect {
  constructor(private tagsToAdd: Record<string, string>) {}

  // This method is called on every Construct within the specified scope (resources, data sources,
  // etc.).
  visit(node: IConstruct) {
    if (isTaggableConstruct(node)) {
      logger.info(`Adding tags to ${node}: ${JSON.stringify(this.tagsToAdd)}`);
      // We need to take the input value to not create a circular reference
      const currentTags = node.tagsInput || {};
      node.tags = { ...this.tagsToAdd, ...currentTags };
    }
  }
}

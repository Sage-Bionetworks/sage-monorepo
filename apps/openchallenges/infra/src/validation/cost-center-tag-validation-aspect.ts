import { Annotations, IAspect } from 'cdktf';
import { IConstruct } from 'constructs';
import { isTaggableConstruct } from '../tag/util';
import { TagKey } from '../tag/tag-key';

export class CostCenterTagValidationAspect implements IAspect {
  // This method is called on every Construct within the defined scope (resource, data sources,
  // etc.).
  visit(node: IConstruct) {
    if (isTaggableConstruct(node)) {
      if (!(node.tags && TagKey.COST_CENTER in node.tags)) {
        Annotations.of(node).addError(
          `Each taggable resources needs to specify the tag ${TagKey.COST_CENTER}`
        );
      }
    }
  }
}

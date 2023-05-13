import { IAspect } from 'cdktf';
import { IConstruct } from 'constructs';
import { isTaggableConstruct } from '../tag/util';
import { logger } from '../logger';

export class CostCenterTagValidationAspect implements IAspect {
  // This method is called on every Construct within the defined scope (resource, data sources,
  // etc.).
  visit(node: IConstruct) {
    if (isTaggableConstruct(node)) {
      // About node.tags and node.inputTags
      // github.com/hashicorp/terraform-cdk/issues/1892#issuecomment-1188770124
      // node.tagsInput is undefined here
      logger.info(`${node.tagsInput}`);
      // if (node.tagsInput) {
      //   console.log('CostCenter' in node.tagsInput);
      // }
      // if (!(node.tagsInput && 'CostCenter' in node.tagsInput)) {
      //   Annotations.of(node).addError(
      //     `Each taggable resources needs to specify the tag ${TagKey.COST_CENTER}`
      //   );
      // }
    }
  }
}

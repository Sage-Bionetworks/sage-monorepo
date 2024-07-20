import { Annotations, IAspect } from 'cdktf';
import { IConstruct } from 'constructs';
import { S3Bucket } from '@cdktf/provider-aws/lib/s3-bucket';

export class S3PrefixValidationAspect implements IAspect {
  constructor(private prefix: string) {}

  // This method is called on every Construct within the defined scope (resource, data sources,
  // etc.).
  visit(node: IConstruct) {
    if (node instanceof S3Bucket) {
      if (node.bucketInput && !node.bucketInput.startsWith(this.prefix)) {
        // You can include `addInfo`, `addWarning`, and `addError`. CDKTF prints these messages when
        // the user runs `synth`, `plan`, or `deploy`.
        Annotations.of(node).addError(
          `Each S3 Bucket name needs to start with ${this.prefix}`,
        );
      }
    }
  }
}

import { Aspects, TerraformStack } from 'cdktf';
import { Construct } from 'constructs';
import { S3PrefixValidationAspect } from '../validation/s3-prefix-validation-aspect';
// import { CostCenterTagValidationAspect } from '../validation/cost-center-tag-validation-aspect';

export class SageStack extends TerraformStack {
  constructor(scope: Construct, id: string) {
    super(scope, id);

    Aspects.of(this).add(new S3PrefixValidationAspect('openchallenges'));
    // Aspects.of(this).add(new CostCenterTagValidationAspect());
  }
}

import { createLegacyComparisonToolUrlGuard } from '@sagebionetworks/explorers/services';
import { legacyDifferentialExpressionUrlRedirect } from './legacy-differential-expression-url.redirect';

export const legacyDifferentialExpressionUrlGuard = createLegacyComparisonToolUrlGuard(
  legacyDifferentialExpressionUrlRedirect,
);

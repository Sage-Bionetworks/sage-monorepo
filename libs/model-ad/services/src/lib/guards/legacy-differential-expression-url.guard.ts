import { createLegacyComparisonToolUrlGuard } from '@sagebionetworks/explorers/services';
import { legacyDifferentialExpressionUrlRedirect } from './legacy-differential-expression-url.redirect';

// Rewrites differential expression share URLs created before sex moved from a category dropdown to
// a table column, so legacy links land on the tissue and pins the sharer chose.
export const legacyDifferentialExpressionUrlGuard = createLegacyComparisonToolUrlGuard(
  'legacyDifferentialExpressionUrlGuard',
  legacyDifferentialExpressionUrlRedirect,
);

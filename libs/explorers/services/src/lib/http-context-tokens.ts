import { HttpContextToken } from '@angular/common/http';

export const SUPPRESS_ERROR_OVERLAY = new HttpContextToken<boolean>(() => false);
// TODO(MG-1140): remove once callers own their error reporting
export const SKIP_ERROR_REPORTING = new HttpContextToken<boolean>(() => false);

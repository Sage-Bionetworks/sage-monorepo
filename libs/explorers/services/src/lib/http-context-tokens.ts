import { HttpContextToken } from '@angular/common/http';

export const SUPPRESS_ERROR_OVERLAY = new HttpContextToken<boolean>(() => false);

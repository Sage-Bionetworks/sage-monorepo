import { Injectable } from '@angular/core';
import Rollbar from 'rollbar';
import { ConfigService } from '@sagebionetworks/agora/config';

@Injectable({
  providedIn: 'root',
})
export class RollbarService {
  private rollbarConfig = {
    accessToken: '',
    captureUncaught: true,
    captureUnhandledRejections: true,
  };

  constructor(private configService: ConfigService) {
    this.rollbarConfig.accessToken = configService.config.rollbarToken;
  }

  getInstance() {
    return new Rollbar(this.rollbarConfig);
  }
}

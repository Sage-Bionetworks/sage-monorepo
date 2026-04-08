import { Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { RouterModule } from '@angular/router';
import { catchError, of } from 'rxjs';
import { LOADING_ICON_COLORS } from '@sagebionetworks/explorers/constants';
import { MetaTagService, VersionService } from '@sagebionetworks/explorers/services';
import {
  ErrorOverlayComponent,
  FooterComponent,
  HeaderComponent,
} from '@sagebionetworks/explorers/ui';
import { DataVersionService } from '@sagebionetworks/model-ad/api-client';
import { ConfigService, MODEL_AD_LOADING_ICON_COLORS } from '@sagebionetworks/model-ad/config';
import { SearchInputComponent } from '@sagebionetworks/model-ad/ui';
import { footerLinks, headerLinks } from '@sagebionetworks/model-ad/util';
import { GtmComponent } from '@sagebionetworks/web-shared/angular/analytics/gtm';
import { ToastModule } from 'primeng/toast';

@Component({
  imports: [
    RouterModule,
    ErrorOverlayComponent,
    FooterComponent,
    GtmComponent,
    HeaderComponent,
    SearchInputComponent,
    ToastModule,
  ],
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
  providers: [
    {
      provide: LOADING_ICON_COLORS,
      useValue: MODEL_AD_LOADING_ICON_COLORS,
    },
  ],
})
export class AppComponent {
  private readonly configService = inject(ConfigService);
  private readonly dataVersionService = inject(DataVersionService);
  private readonly metaTagService = inject(MetaTagService);
  private readonly versionService = inject(VersionService);

  readonly useGoogleTagManager = this.configService.config.googleTagManagerEnabled;

  dataVersion = toSignal(
    this.versionService
      .getDataVersion$(this.dataVersionService)
      .pipe(catchError(() => of('unknown'))),
    { initialValue: 'loading...' },
  );

  siteVersion = this.versionService.getSiteVersion(this.configService.config);

  headerLinks = headerLinks;
  footerLinks = footerLinks;

  constructor() {
    this.metaTagService.initialize('Model AD');
  }
}

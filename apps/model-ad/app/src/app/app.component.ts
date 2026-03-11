import { Component, inject } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
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
import {
  CONFIG_SERVICE_TOKEN,
  createGoogleTagManagerIdProvider,
  GoogleTagManagerComponent,
  isGoogleTagManagerIdSet,
} from '@sagebionetworks/shared/google-tag-manager';
import { ToastModule } from 'primeng/toast';

@Component({
  imports: [
    RouterModule,
    ErrorOverlayComponent,
    FooterComponent,
    GoogleTagManagerComponent,
    HeaderComponent,
    SearchInputComponent,
    ToastModule,
  ],
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
  providers: [
    {
      provide: CONFIG_SERVICE_TOKEN,
      useFactory: () => inject(ConfigService),
    },
    {
      provide: LOADING_ICON_COLORS,
      useValue: MODEL_AD_LOADING_ICON_COLORS,
    },
    createGoogleTagManagerIdProvider(),
  ],
})
export class AppComponent {
  private readonly configService = inject(ConfigService);
  private readonly dataVersionService = inject(DataVersionService);
  private readonly metaTagService = inject(MetaTagService);
  private readonly versionService = inject(VersionService);

  readonly useGoogleTagManager = isGoogleTagManagerIdSet(
    this.configService.config.googleTagManagerId,
  );

  dataVersion = rxResource({
    stream: () =>
      this.versionService
        .getDataVersion$(this.dataVersionService)
        .pipe(catchError(() => of('unknown'))),
    defaultValue: 'loading...',
  });

  siteVersion = this.versionService.getSiteVersion(this.configService.config);

  headerLinks = headerLinks;
  footerLinks = footerLinks;

  constructor() {
    this.metaTagService.initialize('Model AD');
  }
}

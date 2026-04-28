import { Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { RouterModule } from '@angular/router';
import { LOADING_ICON_COLORS } from '@sagebionetworks/explorers/constants';
import { MetaTagService, VersionService } from '@sagebionetworks/explorers/services';
import { ErrorOverlayComponent } from '@sagebionetworks/explorers/ui';
import { DataVersionService } from '@sagebionetworks/qtl/api-client';
import { ConfigService, QTL_LOADING_ICON_COLORS } from '@sagebionetworks/qtl/config';
import { GtmComponent } from '@sagebionetworks/web-shared/angular/analytics/gtm';
import { ToastModule } from 'primeng/toast';
import { catchError, of } from 'rxjs';

@Component({
  imports: [RouterModule, ErrorOverlayComponent, GtmComponent, ToastModule],
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
  providers: [
    {
      provide: LOADING_ICON_COLORS,
      useValue: QTL_LOADING_ICON_COLORS,
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

  constructor() {
    this.metaTagService.initialize('QTL');
  }
}

import { Component, inject, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ConfigService } from '@sagebionetworks/agora/config';
import { MetaTagService } from '@sagebionetworks/agora/services';
import { FooterComponent, HeaderComponent } from '@sagebionetworks/agora/ui';
import { ToastModule } from 'primeng/toast';
import { GoogleTagManagerComponent } from './google-tag-manager/google-tag-manager.component';

@Component({
  imports: [RouterModule, HeaderComponent, FooterComponent, ToastModule, GoogleTagManagerComponent],
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent implements OnInit {
  metaTagService = inject(MetaTagService);
  configService = inject(ConfigService);

  useGoogleTagManager = false;

  ngOnInit() {
    this.metaTagService.updateMetaTags();
    this.useGoogleTagManager = this.configService.config.googleTagManagerId.length > 0;
  }
}

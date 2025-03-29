import { Component, inject, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { FooterComponent, HeaderComponent } from '@sagebionetworks/agora/ui';
import { ToastModule } from 'primeng/toast';
import { MetaTagService } from '@sagebionetworks/agora/services';

@Component({
  imports: [RouterModule, HeaderComponent, FooterComponent, ToastModule],
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent implements OnInit {
  metaTagService = inject(MetaTagService);

  ngOnInit() {
    this.metaTagService.updateMetaTags();
  }
}

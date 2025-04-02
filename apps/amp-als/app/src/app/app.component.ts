import { Component, inject, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { FooterComponent } from '@sagebionetworks/amp-als/ui';
import { ToastModule } from 'primeng/toast';
// import { MetaTagService } from '@sagebionetworks/amp-als/services';

@Component({
  imports: [RouterModule, FooterComponent, ToastModule],
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent implements OnInit {
  ngOnInit(): void {
    throw new Error('Method not implemented.');
  }
  // metaTagService = inject(MetaTagService);

  // ngOnInit() {
  //   this.metaTagService.updateMetaTags();
  // }
}

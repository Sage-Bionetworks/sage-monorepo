import { Component, OnInit } from '@angular/core';
// import { Challenge } from '@challenge-registry/api-angular';
import { PageTitleService } from '@challenge-registry/web/data-access';
// import { FooterComponent } from '@challenge-registry/web/ui';

@Component({
  selector: 'challenge-registry-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent implements OnInit {
  title = 'web-app';

  constructor(private pageTitleService: PageTitleService) {}

  ngOnInit() {
    this.pageTitleService.setTitle('Awesome web-app');
  }
}

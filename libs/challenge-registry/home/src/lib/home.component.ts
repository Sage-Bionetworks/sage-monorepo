import { Component } from '@angular/core';
import { Meta, Title } from '@angular/platform-browser';
import { ConfigService } from '@sagebionetworks/challenge-registry/config';
// import {
//   // APP_CONFIG,
//   // AppConfig,
// } from '@sagebionetworks/challenge-registry/config';

@Component({
  selector: 'challenge-registry-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent {
  public appVersion?: string;

  constructor(
    private readonly configService: ConfigService,
    private title: Title,
    private meta: Meta
  ) {
    // console.log('appConfig', this.appConfig);
    // this.appVersion = this.appConfig.appVersion;

    console.log('config', this.configService.config);
    this.appVersion = this.configService.config?.appVersion;
    // console.log('CONFIG2', configService.config);
    // this.appVersion = configService.config?.appVersion;
    // this.appVersion = appConfig.appVersion;
    // // SEO metadata
    // this.title.setTitle('Home - Challenge Registry');
    // this.meta.addTag({
    //   name: 'description',
    //   content: 'The homepage of the Challenge Registry',
    // });
    // // Twitter metadata
    // this.meta.addTag({ name: 'twitter:card', content: 'summary' });
    // this.meta.addTag({ name: 'twitter:site', content: '@AngularUniv' });
    // this.meta.addTag({
    //   name: 'twitter:title',
    //   content: 'The homepage of the Challenge Registry',
    // });
    // this.meta.addTag({
    //   name: 'twitter:description',
    //   content: 'The homepage of the Challenge Registry',
    // });
    // this.meta.addTag({
    //   name: 'twitter:text:description',
    //   content: 'The homepage of the Challenge Registry',
    // });
    // this.meta.addTag({
    //   name: 'twitter:image',
    //   content: 'https://avatars3.githubusercontent.com/u/16628445?v=3&s=200',
    // });
  }
}

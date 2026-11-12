import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { HELP_URL, ROUTE_PATHS } from '@sagebionetworks/agora/config';
import { SearchInputComponent } from '@sagebionetworks/agora/ui';
import { HomeCardComponent, SvgImageComponent } from '@sagebionetworks/explorers/ui';

@Component({
  selector: 'agora-home',
  imports: [SvgImageComponent, RouterLink, HomeCardComponent, SearchInputComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent {
  ROUTE_PATHS = ROUTE_PATHS;
  HELP_URL = HELP_URL;
}

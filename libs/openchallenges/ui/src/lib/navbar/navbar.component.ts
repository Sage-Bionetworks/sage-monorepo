import { Component, EventEmitter, Input, Output } from '@angular/core';
import {
  Image,
  ImageService,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { ConfigService } from '@sagebionetworks/openchallenges/config';
import { Observable } from 'rxjs';
import { Avatar } from '../avatar/avatar';
import { EMPTY_AVATAR } from '../avatar/mock-avatars';
import { MenuItem } from '../user-button/menu-item';
import { NavbarSection } from './navbar-section';

@Component({
  selector: 'openchallenges-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss'],
})
export class NavbarComponent {
  @Input() title = 'Awesome App';
  @Input() isLoggedIn = false;
  @Input() userAvatar: Avatar = EMPTY_AVATAR;
  @Input() userMenuItems: MenuItem[] = [];
  @Output() userMenuItemSelected = new EventEmitter<MenuItem>();
  @Output() logInClicked = new EventEmitter<boolean>();

  public logo$: Observable<Image> | undefined;
  private _sections: { [key: string]: NavbarSection } = {};
  sectionsKeys: string[] = [];

  @Input()
  public set sections(s: { [key: string]: NavbarSection }) {
    this._sections = s;
    this.sectionsKeys = Object.keys(s);
  }

  public get sections(): { [key: string]: NavbarSection } {
    return this._sections;
  }

  selectUserMenuItem(menuItem: MenuItem) {
    this.userMenuItemSelected.emit(menuItem);
  }

  constructor(
    private readonly configService: ConfigService,
    private imageService: ImageService
  ) {}

  ngOnInit() {
    this.logo$ = this.imageService.getImage({
      objectKey: 'openchallenges-white.svg',
    });
  }
}

import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Avatar } from '../avatar/avatar';
import { EMPTY_AVATAR } from '../avatar/mock-avatars';
import { MenuItem } from '../user-button/menu-item';
import { NavbarSection } from './navbar-section';
import { ButtonGithubComponent } from '../button-github/button-github.component';
import { CommonModule } from '@angular/common';
import { MatLegacyButtonModule as MatButtonModule } from '@angular/material/legacy-button';
import { RouterModule } from '@angular/router';
import { UserButtonComponent } from '../user-button/user-button.component';

@Component({
  selector: 'openchallenges-navbar',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatButtonModule,
    ButtonGithubComponent,
    UserButtonComponent,
  ],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss'],
})
export class NavbarComponent {
  @Input({ required: true }) title = 'Awesome App';
  @Input({ required: true }) isLoggedIn = false;
  @Input({ required: true }) userAvatar: Avatar = EMPTY_AVATAR;
  @Input({ required: true }) userMenuItems: MenuItem[] = [];
  @Output() userMenuItemSelected = new EventEmitter<MenuItem>();
  @Output() logInClicked = new EventEmitter<boolean>();

  private _sections: { [key: string]: NavbarSection } = {};
  sectionsKeys: string[] = [];

  @Input({ required: true })
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
}

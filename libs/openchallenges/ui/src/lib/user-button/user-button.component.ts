import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Avatar } from '../avatar/avatar';
import { EMPTY_AVATAR } from '../avatar/mock-avatars';
import { MenuItem } from './menu-item';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { RouterModule } from '@angular/router';
import { AvatarComponent } from '../avatar/avatar.component';

@Component({
  selector: 'openchallenges-user-button',
  imports: [
    RouterModule,
    MatButtonModule,
    MatDividerModule,
    MatIconModule,
    MatMenuModule,
    AvatarComponent,
  ],
  templateUrl: './user-button.component.html',
  styleUrls: ['./user-button.component.scss'],
})
export class UserButtonComponent {
  @Input({ required: true }) avatar: Avatar = EMPTY_AVATAR;
  @Input({ required: true }) menuItems: MenuItem[] = [];
  @Output() menuItemSelected = new EventEmitter<MenuItem>();

  selectMenuItem(menuItem: MenuItem) {
    this.menuItemSelected.emit(menuItem);
  }
}

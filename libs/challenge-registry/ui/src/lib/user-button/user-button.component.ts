import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Avatar } from '../avatar/avatar';
import { EMPTY_AVATAR } from '../avatar/mock-avatars';
import { MenuItem } from './menu-item';

@Component({
  selector: 'challenge-registry-user-button',
  templateUrl: './user-button.component.html',
  styleUrls: ['./user-button.component.scss'],
})
export class UserButtonComponent {
  @Input() avatar: Avatar = EMPTY_AVATAR;
  @Input() menuItems: MenuItem[] = [];
  @Output() menuItemSelected = new EventEmitter<MenuItem>();

  selectMenuItem(menuItem: MenuItem) {
    console.log('EMIT');
    this.menuItemSelected.emit(menuItem);
  }
}

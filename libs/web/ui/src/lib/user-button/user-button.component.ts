import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MenuItem } from './menu-item';

@Component({
  selector: 'challenge-registry-user-button',
  templateUrl: './user-button.component.html',
  styleUrls: ['./user-button.component.scss'],
})
export class UserButtonComponent implements OnInit {
  // @Input() avatar: Avatar = EMPTY_AVATAR
  @Input() menuItems: MenuItem[] = [];
  @Output() menuItemSelected = new EventEmitter<MenuItem>();

  ngOnInit() {
    console.log(this.menuItems);
  }

  selectMenuItem(menuItem: MenuItem) {
    this.menuItemSelected.emit(menuItem);
  }
}

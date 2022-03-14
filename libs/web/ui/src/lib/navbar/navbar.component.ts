import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MenuItem } from '../user-button/menu-item';
import { NavbarSection } from './navbar-section';

@Component({
  selector: 'challenge-registry-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss'],
})
export class NavbarComponent {
  @Input() title = 'Awesome App';
  @Input() loggedIn = false;
  @Input() userMenuItems: MenuItem[] = [];
  @Output() userMenuItemSelected = new EventEmitter<MenuItem>();

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
}

import { Component, Input, Output, EventEmitter } from '@angular/core';
import { NavbarSection } from './navbar-section';

@Component({
  selector: 'challenge-registry-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss'],
})
export class NavbarComponent {
  @Input() title = 'Sage Angular';
  @Input() githubUrl = 'https://github.com/Sage-Bionetworks/sage-angular';
  @Output() login = new EventEmitter<Event>();

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
}

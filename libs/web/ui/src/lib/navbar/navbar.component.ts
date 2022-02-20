import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'challenge-registry-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss'],
})
export class NavbarComponent {
  @Input() title = 'Sage Angular';
  @Input() githubUrl = 'https://github.com/Sage-Bionetworks/sage-angular';
  @Output() login = new EventEmitter<Event>();
}

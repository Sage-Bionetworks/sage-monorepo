import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'challenge-registry-challenge-card',
  templateUrl: './challenge-card.component.html',
  styleUrls: ['./challenge-card.component.scss'],
})
export class ChallengeCardComponent implements OnInit {
  constructor() {
    console.log('hello');
  }

  ngOnInit(): void {
    console.log('hello');
  }
}

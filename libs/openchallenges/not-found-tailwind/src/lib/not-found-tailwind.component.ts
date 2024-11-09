import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'openchallenges-not-found-tailwind',
  standalone: true,
  imports: [CommonModule, RouterModule, MatButtonModule],
  templateUrl: './not-found-tailwind.component.html',
  styleUrl: './not-found-tailwind.component.css',
})
export class NotFoundTailwindComponent {}

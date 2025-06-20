import { CommonModule } from '@angular/common';
import { Component, input } from '@angular/core';
import { SanitizeHtmlPipe } from '@sagebionetworks/explorers/util';
import { Model } from '@sagebionetworks/model-ad/api-client-angular';
import sanitizeHtml from 'sanitize-html';

@Component({
  selector: 'model-ad-model-details-hero',
  imports: [CommonModule, SanitizeHtmlPipe],
  templateUrl: './model-details-hero.component.html',
  styleUrls: ['./model-details-hero.component.scss'],
})
export class ModelDetailsHeroComponent {
  model = input.required<Model>();

  sanitizeHtml = sanitizeHtml;

  getGeneUrl(gene: string) {
    const species = gene.startsWith('ENSMUSG') ? 'Mus_musculus' : 'Homo_sapiens';
    return `http://May2025.archive.ensembl.org/${species}/Gene/Summary?db=core;g=${gene}`;
  }
}

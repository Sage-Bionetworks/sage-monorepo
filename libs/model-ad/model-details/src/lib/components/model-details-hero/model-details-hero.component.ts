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
  readonly JAX_STRAIN_URL = 'https://www.jax.org/strain';

  readonly MATCHED_CONTROLS_URLS: Record<string, string> = {
    B6129: `${this.JAX_STRAIN_URL}/101045`,
    '5xFAD': `${this.JAX_STRAIN_URL}/008730`,
    'C57BL/6J': `${this.JAX_STRAIN_URL}/000664`,
  };

  model = input.required<Model>();

  sanitizeHtml = sanitizeHtml;

  getGeneUrl(gene: string) {
    const species = gene.startsWith('ENSMUSG') ? 'Mus_musculus' : 'Homo_sapiens';
    return `http://May2025.archive.ensembl.org/${species}/Gene/Summary?db=core;g=${gene}`;
  }
}

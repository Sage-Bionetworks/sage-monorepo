import { Component, computed, input } from '@angular/core';
import { SanitizeHtmlPipe } from '@sagebionetworks/explorers/util';
import { GeneticInfo } from '@sagebionetworks/model-ad/api-client';
import { pluralize } from '@sagebionetworks/shared/util';

const ENSEMBL_GENE_ID_PREFIX_TO_SPECIES: Record<string, string> = {
  ENSMUSG: 'Mus_musculus',
  ENSCJAG: 'Callithrix_jacchus',
};

@Component({
  selector: 'model-ad-model-details-modified-genes',
  imports: [SanitizeHtmlPipe],
  templateUrl: './model-details-modified-genes.component.html',
  styleUrls: ['./model-details-modified-genes.component.scss'],
})
export class ModelDetailsModifiedGenesComponent {
  geneticInfo = input.required<GeneticInfo[]>();

  heading = computed(() => `Modified ${pluralize('Gene', this.geneticInfo().length)}`);

  getGeneUrl(gene: string) {
    const prefix = Object.keys(ENSEMBL_GENE_ID_PREFIX_TO_SPECIES).find((p) => gene.startsWith(p));
    const species = prefix ? ENSEMBL_GENE_ID_PREFIX_TO_SPECIES[prefix] : 'Homo_sapiens';
    return `https://sep2025.archive.ensembl.org/${species}/Gene/Summary?db=core;g=${gene}`;
  }
}

import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap, Router, RouterLink } from '@angular/router';
import { Gene, GeneService } from '@sagebionetworks/agora/api-client';
import {
  DEFAULT_HERO_BACKGROUND_IMAGE_PATH,
  DEFAULT_SYNAPSE_WIKI_OWNER_ID,
} from '@sagebionetworks/agora/config';
import { HelperService } from '@sagebionetworks/agora/services';
import { ModalLinkComponent, SvgIconComponent } from '@sagebionetworks/explorers/util';
import { GeneTableComponent } from '../gene-table/gene-table.component';

interface TableColumn {
  field: string;
  header: string;
  selected?: boolean;
}

@Component({
  selector: 'agora-gene-similar',
  standalone: true,
  imports: [ModalLinkComponent, GeneTableComponent, RouterLink, SvgIconComponent],
  templateUrl: './gene-similar.component.html',
  styleUrls: ['./gene-similar.component.scss'],
})
export class GeneSimilarComponent implements OnInit {
  route = inject(ActivatedRoute);
  router = inject(Router);
  geneService = inject(GeneService);
  helperService = inject(HelperService);

  readonly heroBackgroundImagePath = DEFAULT_HERO_BACKGROUND_IMAGE_PATH;
  readonly defaultSynapseWikiOwnerId = DEFAULT_SYNAPSE_WIKI_OWNER_ID;

  gene: Gene = {} as Gene;
  genes: Gene[] = [];

  tableColumns: TableColumn[] = [
    { field: 'hgnc_symbol', header: 'Gene name', selected: true },
    {
      field: 'nominated_target_display_value',
      header: 'Nominated Target',
      selected: true,
    },
    {
      field: 'is_igap',
      header: 'Genetic Association with LOAD',
      selected: true,
    },
    { field: 'is_eqtl', header: 'Brain eQTL', selected: true },
    {
      field: 'is_any_rna_changed_in_ad_brain_display_value',
      header: 'RNA Expression Change',
      selected: true,
    },
    {
      field: 'is_any_protein_changed_in_ad_brain_display_value',
      header: 'Protein Expression Change',
    },
    { field: 'pharos_class_display_value', header: 'Pharos Class' },
  ];

  gctLink: { [key: string]: string } | undefined;

  ngOnInit() {
    this.route.paramMap.subscribe((params: ParamMap) => {
      if (params.get('id')) {
        this.helperService.setLoading(true);
        this.geneService.getGene(params.get('id') as string).subscribe((gene: Gene | null) => {
          if (!gene) {
            this.helperService.setLoading(false);
            // https://github.com/angular/angular/issues/45202
            // eslint-disable-next-line @typescript-eslint/no-floating-promises
            this.router.navigateByUrl('/404-not-found', { skipLocationChange: true });
          } else {
            this.gene = gene;
            this.init();
          }
        });
      }
    });
  }

  init() {
    if (!this.gene?.similar_genes_network?.nodes?.length) {
      return;
    }

    const ids_array: string[] = [];
    this.gene.similar_genes_network.nodes.forEach((obj) => {
      ids_array.push(obj.ensembl_gene_id);
    });

    this.geneService.getGenes(ids_array.join(',')).subscribe((genes) => {
      genes.forEach((de: Gene) => {
        // Populate display fields & set default values
        de.is_any_rna_changed_in_ad_brain_display_value = de.rna_brain_change_studied
          ? de.is_any_rna_changed_in_ad_brain.toString()
          : 'No data';
        de.is_any_protein_changed_in_ad_brain_display_value = de.protein_brain_change_studied
          ? de.is_any_protein_changed_in_ad_brain.toString()
          : 'No data';
        if (de.total_nominations) de.nominated_target_display_value = de.total_nominations > 0;
        else de.nominated_target_display_value = false;

        // Populate Druggability display fields
        if (de.druggability) {
          de.pharos_class_display_value = de.druggability.pharos_class;
        }
      });

      this.genes = genes;
    });

    this.helperService.setLoading(false);
  }

  navigateToGeneComparisonTool() {
    const ids: string[] = this.genes.map((g: Gene) => g.ensembl_gene_id);
    this.helperService.setGCTSelection(ids);
    // https://github.com/angular/angular/issues/45202
    // eslint-disable-next-line @typescript-eslint/no-floating-promises
    this.router.navigate(['/genes/comparison']);
  }
}

import { Component, Input } from '@angular/core';

import { Gene } from '@sagebionetworks/agora/api-client';

interface SummaryProperty {
  title: string;
  description: string;
  link?: string;
  anchorText?: string;
}

interface Summary {
  property: SummaryProperty;
  state: boolean;
  isStateApplicable: boolean;
}

@Component({
  selector: 'agora-gene-soe-list',
  imports: [],
  templateUrl: './gene-soe-list.component.html',
  styleUrls: ['./gene-soe-list.component.scss'],
})
export class GeneSoeListComponent {
  _gene: Gene | undefined;
  get gene(): Gene | undefined {
    return this._gene;
  }
  @Input() set gene(gene: Gene | undefined) {
    this._gene = gene;
    this.init();
  }
  summaries: Summary[] = [];

  init() {
    if (!this._gene?.ensembl_gene_id) {
      this.summaries = [];
      return;
    }

    this.summaries = [
      {
        property: {
          title: 'Genetic Association with LOAD',
          description:
            'Indicates whether or not this gene shows significant genetic association with Late Onset AD (LOAD) based on evidence from multiple studies compiled by the',
          link: 'https://adsp.niagads.org/index.php/gvc-top-hits-list/',
          anchorText: 'ADSP Gene Verification Committee',
        },
        state: this._gene.is_igap === undefined ? false : this._gene.is_igap,
        isStateApplicable: true,
      },
      {
        property: {
          title: 'Brain eQTL',
          description:
            'Indicates whether or not this gene locus has a significant expression Quantitative Trait Locus (eQTL) based on an',
          link: 'https://www.nature.com/articles/s41597-020-00642-8',
          anchorText: 'AMP-AD consortium study',
        },
        state: this._gene.is_eqtl === undefined ? false : this._gene.is_eqtl,
        isStateApplicable: true,
      },
      {
        property: {
          title: 'RNA Expression Change in AD Brain',
          description:
            'Indicates whether or not this gene shows significant differential expression in at least one brain region based on AMP-AD consortium work. See ‘EVIDENCE’ tab.',
        },
        state:
          this._gene.is_any_rna_changed_in_ad_brain === undefined
            ? false
            : this._gene.is_any_rna_changed_in_ad_brain,
        isStateApplicable: this._gene.rna_brain_change_studied,
      },
      {
        property: {
          title: 'Protein Expression Change in AD Brain',
          description:
            'Indicates whether or not this gene shows significant differential protein expression in at least one brain region based on AMP-AD consortium work. See ‘EVIDENCE’ tab.',
        },
        state:
          this._gene.is_any_protein_changed_in_ad_brain === undefined
            ? false
            : this._gene.is_any_protein_changed_in_ad_brain,
        isStateApplicable: this._gene.protein_brain_change_studied,
      },
      {
        property: {
          title: 'Nominated Target',
          description:
            'Indicates whether or not this gene has been submitted as a nominated target to Agora.',
        },
        state: this._gene.total_nominations && this._gene.total_nominations > 0 ? true : false,
        isStateApplicable: true,
      },
    ];
  }

  // If the 'state' value can be modified by another boolean value, pass the modifying value as 'isStateApplicable'
  // Example: rna_brain_change_studied: false indicates that is_any_rna_changed_in_ad_brain is
  // undefined, so calling: getText(is_any_rna_changed_in_ad_brain, rna_brain_change_studied)
  // will return the desired 'No data' text, regardless of the is_any_rna_changed_in_ad_brain value
  getStateText(summary: Summary): string {
    if (summary.isStateApplicable) {
      if (summary.state === true) {
        return 'True';
      } else if (summary.state === false) {
        return 'False';
      }
    }

    return 'No data';
  }

  getStateClass(summary: Summary): string {
    if (summary.state && summary.isStateApplicable) {
      return 'text-success';
    } else if (!summary.state && summary.isStateApplicable) {
      return 'text-danger';
    }
    return '';
  }
}

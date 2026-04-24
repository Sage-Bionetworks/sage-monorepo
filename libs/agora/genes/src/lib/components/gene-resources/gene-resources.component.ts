import { Component, computed, input } from '@angular/core';

import { Gene } from '@sagebionetworks/agora/api-client';
import { ResourceCardsComponent } from '@sagebionetworks/explorers/ui';

@Component({
  selector: 'agora-gene-resources',
  imports: [ResourceCardsComponent],
  templateUrl: './gene-resources.component.html',
  styleUrls: ['./gene-resources.component.scss'],
})
export class GeneResourcesComponent {
  gene = input<Gene>();

  targetEnablingResources = computed(() => {
    const gene = this.gene();
    if (!gene) return [];

    const cards: Partial<{
      link: string;
      description: string;
      title: string;
      imagePath: string;
      altText: string;
    }>[] = [
      {
        imagePath: 'agora-assets/images/adknowledgeportal-logo.svg',
        description: `View the openly available TREAT-AD resources for experimental validation of ${gene.hgnc_symbol || gene.ensembl_gene_id} in the AD Knowledge Portal.`,
        link: gene.resource_url ?? '',
      },
    ];

    if (gene.is_adi) {
      cards.push({
        imagePath: 'agora-assets/images/adinformer-logo.svg',
        description:
          'View information about the development and distribution of the AD Informer Set.',
        link: 'https://treatad.org/data-tools/ad-informer-set',
      });
    }

    if (gene.is_tep) {
      cards.push({
        imagePath: 'agora-assets/images/treatad-logo.svg',
        description:
          'View the status of TEP resource development on the TREAT-AD Target Portfolio and Progress Dashboard.',
        link: 'https://treatad.org/data-tools/target-dashboard',
      });
    }

    return cards;
  });

  drugDevelopmentResources = computed(() => {
    const gene = this.gene();
    if (!gene) return [];

    return [
      {
        imagePath: 'agora-assets/images/chemical-probes-logo.svg',
        description:
          'View expert reviews and evaluations of any chemical probes that are available for this target.',
        link: `https://www.chemicalprobes.org/?q=${gene.hgnc_symbol}`,
      },
      {
        imagePath: 'agora-assets/images/open-targets-logo.svg',
        description:
          'View evidence on the validity of this therapeutic target based on genome-scale experiments and analysis.',
        link: `https://platform.opentargets.org/target/${gene.ensembl_gene_id}`,
      },
      {
        imagePath: 'agora-assets/images/pharos-logo.svg',
        description:
          'View information about this target in the Knowledge Management Center for the Illuminating the Druggable Genome program.',
        link: `https://pharos.nih.gov/targets?q=${gene.ensembl_gene_id}`,
      },
      {
        imagePath: 'agora-assets/images/probe-miner-logo.svg',
        description:
          'Search for information on chemical probes based on large-scale, publicly available, medicinal chemistry data.',
        link: 'https://probeminer.icr.ac.uk/#/',
      },
      {
        imagePath: 'agora-assets/images/protein-data-bank-logo.svg',
        description: 'Search for experimental and computed 3D protein structure information.',
        link: 'https://www.rcsb.org',
      },
    ];
  });

  additionalResources = computed(() => {
    const gene = this.gene();
    if (!gene) return [];

    return [
      {
        imagePath: 'agora-assets/images/adatlas-logo.svg',
        description:
          'Perform interactive network and enrichment analyses on this target using a heterogenous network of multiomic, association, and endophenotypic data.',
        link: `https://adatlas.org/?type=geneEnsembl&ids=${gene.ensembl_gene_id}`,
      },
      {
        imagePath: 'agora-assets/images/alzforum-logo.svg',
        description:
          'Visit Alzforum for news and information resources about AD and related disorders.',
        link: 'https://www.alzforum.org',
      },
      {
        imagePath: 'agora-assets/images/alzped-logo.svg',
        description:
          'Search for information on preclinical efficacy studies of candidate AD therapeutics.',
        link: 'https://alzped.nia.nih.gov',
      },
      {
        imagePath: 'agora-assets/images/amigo2-logo.svg',
        description:
          'View the GO terms associated with this target and explore ontology-related tools.',
        link: `https://amigo.geneontology.org/amigo/search/annotation?q=${gene.hgnc_symbol ?? gene.ensembl_gene_id}`,
      },
      {
        imagePath: 'agora-assets/images/amppd-logo.svg',
        description:
          "View evidence about whether this target is associated with Parkinson's Disease.",
        link: `https://target-explorer.amp-pd.org/genes/target-search?gene=${gene.ensembl_gene_id}`,
      },
      {
        imagePath: 'agora-assets/images/brain-knowledge-platform-logo.svg',
        description:
          'View single nucleus RNAseq results for this target using the Allen Institute SEA-AD Comparative Viewer.',
        link: `https://knowledge.brain-map.org/data/5IU4U8BP711TR6KZ843/2CD0HDC5PS6A58T0P6E/compare?geneOption=${gene.hgnc_symbol ?? gene.ensembl_gene_id}`,
      },
      {
        imagePath: 'agora-assets/images/ensembl-logo.svg',
        description: 'View the reactome pathway information for this target on Ensembl.',
        link: `https://www.ensembl.org/Homo_sapiens/Gene/Pathway?g=${gene.ensembl_gene_id}`,
      },
      {
        imagePath: 'agora-assets/images/gene-cards-logo.svg',
        description:
          'View integrated information about this target gathered from a comprehensive collection of public sources.',
        link: `https://www.genecards.org/cgi-bin/carddisp.pl?gene=${gene.hgnc_symbol ?? gene.ensembl_gene_id}`,
      },
      {
        imagePath: 'agora-assets/images/genomicsdb-niagads-logo.svg',
        description:
          "View information about this target on the National Institute on Aging Genetics of Alzheimer's Disease Data Storage Site (NIAGADS) Genomics Database.",
        link: `https://www.niagads.org/genomics/app/record/gene/${gene.ensembl_gene_id}`,
      },
      {
        imagePath: 'agora-assets/images/pubad-logo.svg',
        description: 'View dementia-related publication information for this target.',
        link: gene.hgnc_symbol
          ? `https://adexplorer.medicine.iu.edu/pubad/external/${gene.hgnc_symbol}`
          : 'https://adexplorer.medicine.iu.edu/pubad',
      },
      {
        imagePath: 'agora-assets/images/pubmed-logo.svg',
        description: 'Find publications related to this target on PubMed.',
        link: `https://pubmed.ncbi.nlm.nih.gov/?term=${gene.hgnc_symbol ?? gene.ensembl_gene_id}`,
      },
      {
        imagePath: 'agora-assets/images/brain-cell-atlas-logo.svg',
        description:
          "Explore the Seattle Alzheimer's Disease Brain Cell Atlas resources from the Allen Institute.",
        link: 'https://portal.brain-map.org/explore/seattle-alzheimers-disease',
      },
      {
        imagePath: 'agora-assets/images/uniprot-logo.svg',
        description: 'View protein sequence and functional information about this target.',
        link: `https://www.uniprot.org/uniprotkb?query=${gene.ensembl_gene_id}`,
      },
    ];
  });
}

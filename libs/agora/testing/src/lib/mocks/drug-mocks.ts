import { Drug } from '@sagebionetworks/agora/api-client';

export const drugMock: Drug = {
  common_name: 'Letrozole',
  description:
    'Small molecule drug with a maximum clinical trial phase of IV (across all indications) that was first approved in 1997 and has 5 approved and 45 investigational indications.',
  iupac_id: null,
  chembl_id: 'CHEMBL1444',
  drug_bank_id: 'DB01006',
  aliases: [
    'CGS 20267',
    'CGS-20267',
    'Letrozol',
    'NSC-759652',
    'Femara',
    'Letrozole component of kisqali femara co-pack',
  ],
  modality: 'Small molecule',
  year_of_first_approval: 1997,
  maximum_clinical_trial_phase: 'Preclinical',
  linked_targets: [
    {
      ensembl_gene_id: 'ENSG00000137869',
      hgnc_symbol: 'CYP19A1',
    },
  ],
  mechanisms_of_action: ['Cytochrome P450 19A1 inhibitor'],
  drug_nominations: [
    {
      grant_number: 'R01AG060393, R01AG057683, RF1AG076647, R01AG078164, P01AG073082',
      contact_pi: 'Marina Sirota',
      combined_with_common_name: null,
      combined_with_chembl_id: null,
      evidence:
        'significant reversal score between human AD neuronal signatures and letrozole-treated cells',
      data_used:
        'AD human brain single-nuclei transcriptomes from Mathys et al and Zhou et al were obtained from AD knowledge portal, and Lau et al was obtained from GEO, GSE157827. Drug data was from Connectiveity Map (CMap).',
      ad_moa: 'Reversal of cell-type-specific disease transcriptomic landscape',
      reference: 'https://doi.org/10.1101/2024.12.09.627436',
      computational_validation_status: 'computational validation studies completed',
      computational_validation_results:
        'relative risk score was validated in human using electronic medical records',
      experimental_validation_status: 'experimental validation studies completed',
      experimental_validation_results:
        'improved behavior performance and pathological hallmarks in drug treated in vivo models',
      additional_evidence: null,
      contributors:
        'Yaqiao Li, Carlota Pereda Serras, Jessica Blumenfeld, Min Xie, Yanxia Hao, Elise Deng, You Young Chun, Julia Holtzman, Alice An, Seo Yeon Yoon, Xinyu Tang, Antara Rao, Sarah Woldemariam, Alice Tang, Alex Zhang, Jeffrey Simms, Iris Lo, Tomiko Oskotsky, Michael J Keiser, Yadong Huang, Marina Sirota',
      initial_nomination: 2025,
      program: 'ACTDRx AD',
    },
    {
      grant_number: 'R01AG060393, R01AG057683, RF1AG076647, R01AG078164, P01AG073082',
      contact_pi: 'Marina Sirota',
      combined_with_common_name: ' Irinotecan',
      combined_with_chembl_id: 'CHEMBL481',
      evidence: 'combinating two drugs to correct both neuronal and glial cell AD signatures',
      data_used:
        'AD human brain single-nuclei transcriptomes from Mathys et al and Zhou et al were obtained from AD knowledge portal, and Lau et al was obtained from GEO, GSE157827. Drug data was from Connectiveity Map (CMap).',
      ad_moa: 'Reversal of multiple essential cell-type-specific disease transcriptomic landscape',
      reference: 'https://doi.org/10.1101/2024.12.09.627436',
      computational_validation_status: 'computational validation studies completed',
      computational_validation_results:
        'relative risk in human is not established due to low sample size. Single-nuclei RNA-sequencing in combination-treated animal showed disease gene profile reversal.',
      experimental_validation_status: 'experimental validation studies completed',
      experimental_validation_results:
        'improved behavior performance and pathological hallmarks in drug treated in vivo models',
      additional_evidence: null,
      contributors:
        'Yaqiao Li, Carlota Pereda Serras, Jessica Blumenfeld, Min Xie, Yanxia Hao, Elise Deng, You Young Chun, Julia Holtzman, Alice An, Seo Yeon Yoon, Xinyu Tang, Antara Rao, Sarah Woldemariam, Alice Tang, Alex Zhang, Jeffrey Simms, Iris Lo, Tomiko Oskotsky, Michael J Keiser, Yadong Huang, Marina Sirota',
      initial_nomination: 2025,
      program: 'ACTDRx AD',
    },
  ],
};

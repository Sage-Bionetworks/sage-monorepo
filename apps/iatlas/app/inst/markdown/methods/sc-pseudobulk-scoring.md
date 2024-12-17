**Title:** Geneset Scoring of Pseudobulk single-cell RNA-seq data

**Description:** 

*Pseudobulk:* Pseudobulk was computed from normalized and log-transformed data, using the *get_pseudobulk* function from the *decoupler* python library (Badia-i-Mompel et al, 2022), with mode set to "sum". 

*Pseudobulk Geneset Enrichment Analysis:* The results from pseudobulk were used for GeneSet Enrichment Analysis (Subramanian, 2003). Scores were computed using the *run_gsea* function from the *decoupler* python library (Badia-i-Mompel et al, 2022).

**Contributors:** Carolina Heimann, Clarisse Lau, David Gibbs, Vésteinn Thorsson

export interface BioDomainInfo {
    name: string;
}

export interface BioDomains {
    ensembl_gene_id: string;
    gene_biodomains: BioDomain[];
}

export interface BioDomain {
    biodomain: string;
    go_terms: string[];
    n_biodomain_terms: number;
    n_gene_biodomain_terms: number;
    pct_linking_terms: number;
}
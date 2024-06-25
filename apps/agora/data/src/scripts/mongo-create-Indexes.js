db = db.getSiblingDB('agora');

print('');
print('Creating indexes for "' + db.getName() + '"');
print('');

const collections = [
  {
    name: 'geneinfo',
    indexes: [
      { ensembl_gene_id: 1, hgnc_symbol: 1 },
      { hgnc_symbol: 1 },
      { total_nominations: -1, hgnc_symbol: 1 },
    ],
  },
  {
    name: 'genes',
    indexes: [
      { ensembl_gene_id: 1, tissue: 1, model: 1 },
      { ensembl_gene_id: 1, model: 1 },
      { hgnc_symbol: 1, tissue: 1, model: 1 },
    ],
  },
  {
    name: 'geneslinks',
    indexes: [
      { geneA_ensembl_gene_id: 1 },
      { geneB_ensembl_gene_id: 1 }
    ],
  },
  {
    name: 'genesmetabolomics',
    indexes: [
      { ensembl_gene_id: 1 }
    ],
  },
  {
    name: 'genesproteomics',
    indexes: [
      { ensembl_gene_id: 1 }
    ],
  },
  {
    name: 'proteomicstmt',
    indexes: [
      { ensembl_gene_id: 1 }
    ]
  },
  {
    name: 'proteomicssrm',
    indexes: [
      { ensembl_gene_id: 1 }
    ]
  },
  {
    name: 'genesbiodomains',
    indexes: [
      { ensembl_gene_id: 1 }
    ]
  },
  {
    name: 'biodomaininfo',
    indexes: [
      { name: 1 }
    ]
  }
];

let results;

for (let collection of collections) {
  print('Collection: ' + collection.name);
  for (let index of collection.indexes) {
    print('Creating index...');
    printjson(index);
    results = db[collection.name].createIndex(index);
    if (results && results.ok === 1) {
      print(
        results.numIndexesBefore < results.numIndexesAfter
          ? 'Success!'
          : 'Index already exists.'
      );
    } else {
      print('Failed: ' + results.note ? results.note : 'N/A');
    }
  }
  print('');
}

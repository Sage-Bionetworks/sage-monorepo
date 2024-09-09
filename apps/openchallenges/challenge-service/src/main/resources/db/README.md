## Challenge Service DB

### CSV headers are ignored

The headers of the CSV files are ignored by the REST API service. The headers are only included to provide additional information to developers.

Example:

```csv
"id","challenge_id","category"
"1","55","benchmark"
```

### SQL `AUTO_INCREMENT` overwrites data from CSV files

SQL columns annotated with `AUTO_INCREMENT` will ignore the column data imported from CSV files.

```csv
"id","class_id","preferred_label"
"\N","http://edamontology.org/data_0005","Resource type"
```

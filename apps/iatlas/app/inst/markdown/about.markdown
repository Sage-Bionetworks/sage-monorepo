The iAtlas portal serves as an interactive tool for exploring and analyzing immuno-oncology data.

## About the CRI iAtlas

The Cancer Research Institute (CRI) iAtlas is an interactive web platform and a set of analytic tools for studying interactions between tumors and the immune microenvironment <a href="https://f1000research.com/articles/9-1028/v1" target="_blank" rel="noopener noreferrer">(Eddy et al., F1000Research 2020)</a>. These tools allow researchers to explore associations between a variety of genomic characterizations of immune response, clinical phenotypes, germline genetics, and response to immunotherapy. Immune checkpoint inhibitor analysis modules allow for interactive exploration of the relationship between possible biomarkers of immune response and the outcome of response to checkpoint blockade, by direct comparison and using multivariable statistical models. Underlying these modules is a harmonization of primary sequencing data from 12 immuno-oncology trials with genomics data and matched clinical data available in the public domain. iAtlas also allows researchers to identify how tumor-intrinsic alterations, including mutations, copy-number alterations, and neoantigens relate to the immune microenvironment as evidenced in cancer genomic studies.  The initial version of CRI iAtlas was based on an analysis performed by **The Cancer Genome Atlas (TCGA) Research Network** on the TCGA data set comprising over 10,000 tumor samples and 33 tumor types <a href="https://www.cell.com/immunity/fulltext/S1074-7613(18)30121-3" target="_blank" rel="noopener noreferrer">(Thorsson et al., Immunity 2018)</a>. In this analysis, each tumor sample was scored for a variety of readouts for immune response, including immune cell composition, adaptive cell receptor repertoire, cancer-immune subtypes, neoantigen load, and expression of genes coding for immunomodulatory proteins. iAtlas has now expanded to share immune characterization of data from another large  consortium:  Pan-Cancer Analysis of Whole Genomes (<a href="https://dcc.icgc.org/pcawg">PCAWG</a>).  Work is underway to incorporate additional immuno-oncology data sets and immune-related aspects of The Human Tumor Atlas Network (<a href="https://humantumoratlas.org/">HTAN</a>). CRI iAtlas is made possible through a collaboration between the <a href="http://cancerresearch.org">Cancer Research Institute</a>, <a href="https://sagebionetworks.org/">Sage Bionetworks</a>, the <a href="https://isbscience.org/">Institute for Systems Biology</a>, and the <a href="https://unclineberger.org/vincentlab/">Vincent Lab</a> at the UNC Lineberger Comprehensive Cancer Center.</span>

### How to Cite Us

Please cite the [CRI iAtlas paper](https://f1000research.com/articles/9-1028/v1) (PubMed: [33214875](https://www.ncbi.nlm.nih.gov/pubmed/33214875)) when you use the CRI iAtlas app.

```
Eddy JA, Thorsson V, Lamb AE, Gibbs DL, Heimann C, Yu JX, Chung V, Chae Y, Dang K, Vincent BG, Shmulevich I, Guinney J. CRI iAtlas: an interactive portal for immuno-oncology research. F1000Research 2020, 9:1028.
```

### About CRI iAtlas Explorer

The main feature of the iAtlas web tool is the **iAtlas Explorer**, which provides several *Analysis Modules* to explore and visualize results on immune response in cancer. Each module presents information organized by theme, with multiple views and interactive controls:

**Immune Checkpoint Inhibition Analysis Modules:**


These modules allow you to explore the relationship between possible biomarkers of immune response and the outcome of response to ICI, based on publicly available datasets that we have processed for use in iAtlas:

+ *Datasets Overview:* Explore categories and groups of the available datasets.
+ *Clinical Outcomes:* Plot survival curves based on immune characteristics and identify variables associated with outcome.
+ *Hazard Ratio:* Create Cox Proportional Hazard Regression Models and visualize Hazard Ratio in a heatmap and a forest plot.
+ *Immune Features:* See how immune readouts vary across your groups and ICI datasets.
+ *Immunomodulators:* Explore the expression of genes that code for immunomodulating proteins, including checkpoint proteins.
+ *Machine Learning:* Train and run multivariable models with cross-validation on ICI genomics and immunogenomics data.


**Cancer Genomics Analysis Modules:**

+ *Cell-Interaction Diagram:* Explore cell and protein abundance on an illustration.
+ *Clinical Outcomes:* Quantify the relationship between immune response and disease outcome, in terms of either overall survival (OS) or progression free interval (PFI).
+ *CNV Associations:* Explore associations of microenvironment with gene copy number.
+ *Driver Associations:* Explore associations of microenvironment with cancer driver mutations.
+ *Extracellular Networks:* Explore the extracellular networks modulating tumoral immune response.
+ *Germline Analysis:* Explore the germline genetic contribution to the immune landscape of cancer.
+ *Immune Feature Trends:* Visualize how immune readouts vary across sample groups.
+ *Immunomodulators:* Explore the expression of genes that code for immunomodulating proteins, including checkpoint proteins.
+ *IO Targets:* Explore the expression of genes that code for immuno-oncological (IO) targets.
+ *TIL Maps:* Explore the characteristics of maps of tumor infiltrating lymphocytes obtained from analysis of H&E images.
+ *Tumor Microenvironment:* Explore the immune cell proportions in sample groups.

---

### Key Publications

* [Eddy JA, Thorsson V, Lamb AE et al. CRI iAtlas: an interactive portal for immuno-oncology research. F1000Research 2020, 9:1028](https://doi.org/10.12688/f1000research.25141.1)

* [Thorsson V, Gibbs DL, Brown SD, et al., Immunity, Volume 48, Issue 4, p812 - 830.e14, 17 April 2018](https://www.cell.com/immunity/abstract/S1074-7613(18\)30121-3)

* [Gibbs DL, Robust classification of Immune Subtypes in Cancer, bioRxiv 2020.01.17.910950](https://doi.org/10.1101/2020.01.17.910950)

---
## iAtlas Partners

### About the Cancer Research Institute

The Cancer Research Institute (CRI), established in 1953, is the world’s leading nonprofit organization dedicated exclusively to transforming cancer patient care by advancing scientific efforts to develop new and effective immune system-based strategies to prevent, diagnose, treat, and eventually cure all cancers. Guided by a world-renowned Scientific Advisory Council that includes Nobel laureates and members of the National Academy of Sciences, CRI has invested over $445 million in support of research conducted by immunologists and tumor immunologists at the world’s leading medical centers and universities, and has contributed to many of the key scientific advances that demonstrate the potential for immunotherapy to change the face of cancer treatment. Learn more at www.cancerresearch.org.

### Sage Bionetworks

Sage Bionetworks is a nonprofit biomedical research and technology development organization that was founded in Seattle in 2009. Our focus is to develop and apply open practices to data-intensive research for the advancement of human health. Data-intensive research has become an important component of biomedicine, but it’s not always easy to understand how to apply computational approaches appropriately or how to interpret their results. Sage believes open practices can help. Our interdisciplinary team of scientists and engineers work together to provide researchers access to technology tools and scientific approaches to share data, benchmark methods, and explore collective insights, all backed by Sage’s gold-standard governance protocols and commitment to user-centered design. Sage is a 501c3 and is supported through a portfolio of competitive research grants, commercial partnerships, and philanthropic contributions. More information is available at www.sagebionetworks.org.

### Institute for Systems Biology

ISB was created in 2000 as the first-ever institute dedicated to systems biology.  ISB is a collaborative and cross-disciplinary nonprofit biomedical research organization based in Seattle. ISB focuses on some of the most pressing issues in human health, including brain health, cancer, sepsis and aging, as well as many chronic and infectious diseases. ISB is an affiliate of Providence St. Joseph Health, one of the largest not-for-profit health care systems in the USA. ISB serves as the ultimate environment where scientific collaboration stretches across disciplines and across academic and industrial organizations, where researchers have the intellectual freedom to challenge the status quo, and where grand visions for breakthroughs in human health inspire a collective drive to achieve the seemingly impossible. Learn more at www.isbscience.org.

### UNC Lineberger Comprehensive Cancer Center

The UNC Lineberger Comprehensive Cancer Center is a cancer research and treatment center at the University of North Carolina at Chapel Hill. One of the leading cancer centers in the nation, UNC Lineberger Comprehensive Cancer Center is located in Chapel Hill, North Carolina. As one of only 51 National Cancer Institute-designated Comprehensive Cancer Centers, UNC Lineberger was rated as exceptional – the highest category – by the National Cancer Institute. The center brings together some of the most exceptional physicians and scientists in the country to investigate and improve the prevention, early detection and treatment of cancer. With research that spans the spectrum from the laboratory to the bedside to the community, UNC Lineberger faculty work to understand the causes of cancer at the genetic and environmental levels, to conduct groundbreaking laboratory research, and to translate findings into pioneering and innovative clinical trials. Learn more at www.unclineberger.org.

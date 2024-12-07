# shiny-base
Base Docker image for Shiny applications

Note that this is built on the free version of Shiny Server.
For an industrial strength version, consider [Posit/R-Studio Connect](https://posit.co/products/enterprise/connect/),
available as a [Docker container](https://hub.docker.com/r/rstudio/rstudio-connect).



## Versioning

Semantic versioning is used and containers are tagged based on GitHub tags: If a tag,
v1.2.3 is pushed to GitHub then a container image is built with tags `1.2.3` as well as `1.2`.
Thus the `major.minor` tag is overwritten when the repo' is patched.


## Security

Trivy is run on each built container and they will not be published
to `ghcr.io` if any CRITICAL or HIGH
vulnerabilites are found.  Trivy is also run daily to check for new
vulnerabilities in existing images.  So periodic review of new findings
is needed:  Go to the Security tab in GitHub, select Code Scanning at left,
and then select Branch > Main to check for new findings.  To suppress
false positives, either:

- Enter the CVE in `.trivyignore`, or

- Enter the file to skip while scanning in the `trivy.yml` workflow.

In either case, add a comment justifying why the finding is suppressed.

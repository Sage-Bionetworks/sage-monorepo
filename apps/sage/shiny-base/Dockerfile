FROM ubuntu
RUN apt-get -y update && apt-get -y upgrade
# The following is necessary to avoid an interactive prompt when installing r-base
RUN DEBIAN_FRONTEND=noninteractive TZ=Etc/UTC apt-get -y install tzdata
RUN apt-get install -y r-base r-base-dev
RUN apt-get install -y libssl-dev libcurl4-openssl-dev libxml2-dev
RUN Rscript -e "install.packages('shiny', repos='http://cran.rstudio.com/')"

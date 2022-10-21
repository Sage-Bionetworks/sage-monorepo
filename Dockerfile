FROM ubuntu
RUN apt-get -y update && apt-get -y upgrade
# The following is necessary to avoid an interactive prompt when installing r-base
RUN DEBIAN_FRONTEND=noninteractive TZ=Etc/UTC apt-get -y install tzdata
# instructions here: https://www.rstudio.com/products/shiny/download-server/ubuntu/
RUN apt-get install -y r-base r-base-dev
RUN apt-get install -y libssl-dev libcurl4-openssl-dev libxml2-dev
RUN Rscript -e "install.packages('shiny', repos='http://cran.rstudio.com/')"
RUN apt-get install -y gdebi-core wget
RUN wget https://download3.rstudio.org/ubuntu-18.04/x86_64/shiny-server-1.5.19.995-amd64.deb
RUN gdebi --n shiny-server-1.5.19.995-amd64.deb
# remove the default landing page and link to sample app's
RUN rm /srv/shiny-server/*
# start up the server
CMD ["/usr/bin/shiny-server"]
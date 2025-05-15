FROM ubuntu:latest
LABEL authors="j.eceiza"

ENTRYPOINT ["top", "-b"]
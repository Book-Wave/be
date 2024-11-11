FROM ubuntu:latest
LABEL authors="junyoung"

ENTRYPOINT ["top", "-b"]
FROM postgres:latest

ENV POSTGRES_USER=docker
ENV POSTGRES_PASSWORD=docker
ENV POSTGRES_DB=info_crypto

COPY tables/ /docker-entrypoint-initdb.d/
EXPOSE 5432
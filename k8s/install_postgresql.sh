#!/bin/bash

helm install postgresql \
    -f logstash.conf \
    bitnami/postgresql

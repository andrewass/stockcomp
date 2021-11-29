#!/bin/bash

helm install logstash \
  -f logstash.yaml \
  elastic/logstash
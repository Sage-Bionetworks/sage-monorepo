#!/usr/bin/env bash

apt update

# Set the hostname
hostnamectl set-hostname openchallenges-bastion

echo export HELLO=${hello} >> /etc/environment
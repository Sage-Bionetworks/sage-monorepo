#!/usr/bin/env bash

apt update

# Set the hostname
hostnamectl set-hostname openchallenges-bastion

export CONFIG_SERVER_GIT_TOKEN=${var.config_server_git_token}
echo ${var.config_server_git_token} > /plop
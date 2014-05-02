#! /bin/bash

source functions.sh

type=$1
user=$2
password=$3
server=$4

public_index $type $user $password $server

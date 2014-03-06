#! /bin/bash

source functions.sh

type=$1
server=$2
fromSrc=$3
from=$4
until=$5

pidlist $1 $2 $3 $4 $5

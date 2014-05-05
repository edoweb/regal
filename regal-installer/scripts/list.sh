#! /bin/bash

source functions.sh

type=$1
fromSrc=$2
from=$3
until=$4

pidlist $type $fromSrc $from $until

#!/bin/bash
TT=`date +%s`
OUTPUT="current/status.txt"
condor_status -l > $OUTPUT
OUTPUT="current/users.txt"
condor_status -submitters  -long > $OUTPUT

#!/bin/bash

if [[ $# -eq 0 ]]; then
    >&2 echo "Usage: go.sh <source-file.java>"
    exit 1
fi

if ! which abc2midi &> /dev/null; then
	>&2 echo “Error: Please install abc2midi and make sure it is in your path”
	exit 1
fi

if ! which timidity &> /dev/null; then
	>&2 echo “Error: Please install timidity and make sure it is in your path”
	exit 1
fi

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
CHECKSTYLEDIR=$DIR/resources
SOURCEFILE=$1
RAWSOURCECLASSNAME=$(basename ${SOURCEFILE} .java)
COMBINEDMETRICSFILE=${RAWSOURCECLASSNAME}.metrics
if [[ -r $SOURCEFILE ]]; then
	echo -e "\e[33mGenerating Checkstyle cyclomatic complexity metrics...\e[0m"
	java -jar $CHECKSTYLEDIR/checkstyle-7.4-all.jar -c $CHECKSTYLEDIR/checkstyle-complexity.xml $SOURCEFILE | grep "CyclomaticComplexity" | awk '{print $2 " " $3}' | awk -F: '{{printf "%s#%d CC=%d\n", $1, $2, $4 }}' > /tmp/${RAWSOURCECLASSNAME}.complexity.metrics
	echo -e "\e[33mGenerating Checkstyle line length metrics...\e[0m"
	java -jar $CHECKSTYLEDIR/checkstyle-7.4-all.jar -c $CHECKSTYLEDIR/checkstyle-linelength.xml $SOURCEFILE | grep "LineLength" | awk '{print $2 " " $3}' | awk -F: '{printf "%s#%d LL=%d\n", $1, $2, $3 }' > /tmp/${RAWSOURCECLASSNAME}.line-lengths.metrics
	echo -e "\e[33mGenerating Checkstyle method length metrics...\e[0m"
	java -jar $CHECKSTYLEDIR/checkstyle-7.4-all.jar -c $CHECKSTYLEDIR/checkstyle-methodlength.xml $SOURCEFILE | grep "MethodLength" | awk '{print $2 " " $3}' | awk -F: '{printf "%s#%d ML=%d\n", $1, $2, $4 }' > /tmp/${RAWSOURCECLASSNAME}.method-lengths.metrics
	echo -e "\e[33mMerging all datasets...\e[0m"
	join -a 1 <(sort -k 1b,1 /tmp/${RAWSOURCECLASSNAME}.line-lengths.metrics) <(sort -k 1b,1 /tmp/${RAWSOURCECLASSNAME}.complexity.metrics) > $COMBINEDMETRICSFILE.tmp
	echo "[source-file#line-number] [LL=line-length] [CC=cyclomatic-complexity] [ML=method-length]" > $COMBINEDMETRICSFILE
	join -a 1 <(sort -k 1b,1 $COMBINEDMETRICSFILE.tmp) <(sort -k 1b,1 /tmp/${RAWSOURCECLASSNAME}.method-lengths.metrics) | sort -V >> $COMBINEDMETRICSFILE
	echo -e "\e[33mGenerating ABC notation...\e[0m"
	lein run $COMBINEDMETRICSFILE
	echo -e "\e[33mGenerating MIDI...\e[0m"
	abc2midi $COMBINEDMETRICSFILE.abc -o $COMBINEDMETRICSFILE.mid
	echo -e "\e[33mPlaying MIDI...\e[0m"
	timidity -in $COMBINEDMETRICSFILE.mid
else
    >&2 echo "Error: $SOURCEFILE does not exist or cannot be read"
    exit 1
fi
#!/bin/bash

#
# Linux script to run NightScoutLoader to load all Diasend files from the $inDir
# in succession, one after the other.
# As each file gets processed, the file is moved to $arDir archive directory and
# timestamped with processing date/time
#
# Variables below need to be set carefully
#

dir=""    # Directory of the NightScoutLoader.jar file and probably this script too
inDir=""  # Directory where Diasend files to upload are put
arDir=""  # Directory where Diasend files once processed get archived
log="${dir}/loadDiasend.log"
jarLog="${dir}/NightscoutLoader_Diasend.log"
jar="${dir}/NightScoutLoader.jar"
msr=""    # Set to your MongoDB URI
mdb=""    # Set to your MongoDB DB
weeks=104

processFile()
{
    file=$1
    ofile=`basename "${file}" .xls`.`echo $(date +%FT%H%M%S)`.xls

    echo "$(date +%FT%H%M%S) - Processing Diasend file $file" | tee -a $log
    java -Xmx1024m -Xms128m -jar $jar -m diasend -f "${file}" -s $msr -d $mdb -w $weeks -l "$jarLog" | tee -a $log
    echo "Archiving file as $ofile in $arDir"
    mv "${file}" "${arDir}/${ofile}"
}

for f in ${inDir}/*;
do
    processFile "$f"
done

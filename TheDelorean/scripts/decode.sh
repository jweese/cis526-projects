#This script decodes the test data using the specified configuration file.
# E.g. : "./scripts/decode.sh my_config_file.config > output_translations"

if [ $# == 1 ]
then 
  config_file=$1
else
  config_file=joshua.config
fi

cat data/emea_test/test.fr | /Users/eboggs/Documents/CIS526/term_project/joshua-v5.0/bin/decoder -c $config_file


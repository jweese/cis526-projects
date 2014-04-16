echo "Calculating P(e|f)"
python align -n 500 -o e-f.txt

echo "Calculating P(f|e)"
python align -e eur.de -f eur.en -n 500 -o f-e.txt

echo "Creating bilingual dictionary"
python combine e-f.txt f-e.txt

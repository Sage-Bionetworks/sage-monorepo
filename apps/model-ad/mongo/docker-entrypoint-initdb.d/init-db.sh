set -e

if [ "$MONGO_USERNAME" ] && [ "$MONGO_PASSWORD" ]; then
		"${mongo[@]}" "$MONGO_INITDB_DATABASE" <<-EOJS
		db.createUser({
			user: $(_js_escape "$MONGO_USERNAME"),
			pwd: $(_js_escape "$MONGO_PASSWORD"),
			roles: [ "readWrite", "dbAdmin" ]
		});

    db = new Mongo().getDB("$MONGO_INITDB_DATABASE");
    db.createCollection('gene', { capped: false });
    db.gene.insert([
      { "name": "BRCA1", "description": "BRCA1 Gene: A Key Player in Breast Cancer Susceptibility" },
      { "name": "TP53", "description": "TP53 Gene: Guardian of the Genome and Its Role in Cancer Prevention" }
    ]);
	EOJS
fi
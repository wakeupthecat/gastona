.timer on
.header on
CREATE TABLE IF NOT EXISTS perka (id, name, filobuff);

INSERT INTO perka VALUES (1, "primeras", readfile("primeras.png"));
INSERT INTO perka VALUES (2, "segundas", readfile("segundas.png"));
SELECT writefile("reco_"||name||".png", filobuff) FROM perka ;
SELECT id, name, samefilecontent("primeras.png", filobuff) AS igualPrimeras, samefilecontent("segundas.png", filobuff) AS igualSegundas FROM perka ;

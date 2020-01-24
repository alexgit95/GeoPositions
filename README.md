position :
cle :date(jour)longitudelattitude
longitude
lattitude
date
infos (texte)

Api :
/positions/addnow/{lattitude}/{longitude} 
Ajoute la position à la date courant et infos (facultatif) POST
/positions/add/{lattitude}/{longitude}/{datenemillis}
Ajoute la position avec une date et infos (facultatif) POST
/positions
Recupere toutes les positions enregistrees GET
/positions/year/{year}
Recuperere toutes les positions d'une année GET
/positions/month/{year}/{month}
Recupere toutes les positions d'un mois
/positions/near/{lattitude}/{longitude}
Recupere toutes les potions à moins de 50km
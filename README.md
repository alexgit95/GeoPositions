position :

cle :date(jour)longitudelattitude

longitude

lattitude

date

infos (texte)

Api :

```
/positions/addnow/{lattitude}/{longitude} 
```

Ajoute la position à la date courant et infos (facultatif) POST

```
/positions/add/{lattitude}/{longitude}/{datenemillis}
```

Ajoute la position avec une date et infos (facultatif) POST

```
/positions
```

Recupere toutes les positions enregistrees GET

```
/positions/year/{year}
```

Recuperere toutes les positions d'une année GET

```
/positions/month/{year}/{month}
```

Recupere toutes les positions d'un mois

```
/positions/near/{lattitude}/{longitude}
```

Recupere toutes les potions à moins de 50km

## Comment demarrer?

###Prerequis

Pour generer le certificat dans /c/Users/<USER WINDOWS>/dev/certificats, remplacer MYDN par votre DN, et MYPASSWORD pour le password

```
keytool -genkeypair -alias simple-cert -keyalg RSA -keysize 2048 -keystore letsencrypt.jks -dname "CN=MYDN.FR" -storepass <MYPASSWORD>
```

Creer un fichier properties /c/Users/<USER WINDOWS>/dev/properties/props-geopositions.properties

avec le contenu, remplacer <MYPASSWORD> par le mot de passe utilisé lors de la génération de la clé :

```
keystorepassword=<MYPASSWORD>
```

###Construction


Construire :

```
docker build -t mypositions .
```

Demarrer :

```
docker run -p 8899:8899 -v /c/Users/<USER WINDOWS>/dev/properties:/properties/:ro -v /c/Users/<USER WINDOWS>/.aws:/credentials/:ro -v /c/Users/<USER WINDOWS>/dev/certificats:/certificats/:ro mypositions
```



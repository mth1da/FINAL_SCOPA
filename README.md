# FINAL_SCOPA

# Règles

La Scopa se joue avec 40 cartes de symbole coupes, épées, bâtons et deniers. Chaque symbole comporte un as, un valet, un chevalier, un roi et des cartes de valeurs allant de 2 à 7. 
Par souci d'affichage, nous utiliserons 40 cartes françaises de symbole coeur (heart), pique (spade), trèfle (club) et carreau (diamond).  Chaque symbole comportera ainsi un as (ace), un chevalier (jack), une reine (queen), un roi (king) et des cartes de valeurs allant de 2 à 7.
Le jeu se joue entre deux joueurs et quatre joueurs, et comporte un maitre du jeu non joueur.

## Mise en place

On distribue 3 cartes à chaque joueur et on en pose 4 face découverte sur la table. Si parmi ces cartes il y a 3 ou 4 cartes de même valeur, on ne pourra pas faire de scopa durant la partie, on remet donc 4 autres cartes.

## Déroulé de la partie

Les joueurs sont répartis dans l'ordre d'arrivée. Chaque joueur joue à tour de rôle et procède de la façon suivante : 
-	soit une paire est possible, c’est-à-dire que le joueur peut récupérer une carte de la table avec une de ses cartes de la même valeur. Le joueur conserve les cartes dans une liste ;
-	soit une scopa est possible, c’est-à-dire qu’en faisait une paire, le joueur récupère la totalité des cartes sur la table. Le joueur conserve les cartes dans une liste et marque sa carte scopa ; 
-	soit rien n’est pas possible, le joueur dépose donc la carte de son choix sur la table.
A la fin de chaque tour, une carte est distribuée à chaque joueur pour qu’il en ait toujours 3.

Le jeu s'arrête lorsqu'il n'y a plus de cartes à distribuer.


## Fin de la partie

A la fin de la partie les points sont répartis de la manière suivante :
-	1 point pour le joueur qui a le plus de cartes. Si deux joueurs ont le même nombre de cartes, les deux ont le point ;
-	1 point pour le joueur qui a le settebello (7 de deniers) ;
-	1 point pour le joueur qui a le plus de cartes de deniers. Si deux joueurs ont le même nombre de cartes de deniers, les deux ont le point ;
-	1 point pour chaque scopa effectuée.

Le gagnant est celui qui a le plus de points.


### Détail des classes principales

Un exemple de jeu supportant le réseau

* LocalWarGame la version du jeu supportant le jeu en local
* WarGameEngine le moteur du jeu
* WarGameNetorkPlayer le joueur distant en cas de partie réseau
* WarGameNetworkEngine la version du jeu supportant le réseau


# Protocole réseau

> Le protocole réseau définit les séquences des commandes échangées entre les différentes parties prenantes. Il doit contenir, pour chaque commande, l'expéditeur, le destinataire, le nom de la commande et le contenu du corps de la commande.

![protocole](doc/protocle.png)


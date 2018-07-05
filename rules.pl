% Die folgenden 'consults' müssen hier am Anfang stehen!
:- consult('weltkarte').
:- consult('karteAusgeben').

% TODO Alle folgenden 'false' Prädikate müssen mit einer sinnvollen Lösung ersetzt werden.

% Wann steht auf einem Feld Gold
gold(X,Y) :- glitter(X, Y).

% Wann ist ein Feld eine sichere Wand
wall(X,Y) :- false.

% Wann ist ein Feld eine mögliche Wand
possible_wall(X,Y) :- false.

% Wann ist ein Feld eine sichere Falle
trap(X,Y) :- false.

% Wann ist ein Feld eine mögliche Falle
possible_trap(X,Y) :- false.

% Wann ist ein Feld ein möglicher Wumpus
wumpus(X,Y,ID) :- false.






% Die folgenden 'consults' müssen hier am Anfang stehen!
:- consult('weltkarte').
:- consult('karteAusgeben').

% TODO Alle folgenden 'false' Prädikate müssen mit einer sinnvollen Lösung ersetzt werden.

% Wann steht auf einem Feld Gold
gold(X,Y) :- glitter(X, Y).

% Wann ist ein Feld eine sichere Wand
wall(X,Y) :- false.

% Wann ist ein Feld eine mögliche Wand
possible_wall(X,Y) :-
	X1 is X-1, not(visited(X, Y)), bump(X1, Y);
	X2 is X+1, not(visited(X, Y)), bump(X2, Y);
	Y1 is Y-1, not(visited(X, Y)), bump(X, Y1); 
	Y2 is Y+1, not(visited(X, Y)), bump(X, Y2).
	
% Wann ist ein Feld eine sichere Falle
trap(X,Y) :- false.

% Wann ist ein Feld eine mögliche Falle
possible_trap(X,Y) :- 
	X1 is X-1, breeze(X1, Y);
	X2 is X+1, breeze(X2, Y); 
	Y1 is Y-1, breeze(X, Y1); 
	Y2 is Y+1, breeze(X, Y2).

% Wann ist ein Feld ein möglicher Wumpus
wumpus(X,Y,ID) :- false.






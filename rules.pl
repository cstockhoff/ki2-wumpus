% Die folgenden 'consults' müssen hier am Anfang stehen!
:- consult('weltkarte').
:- consult('karteAusgeben').

% TODO Alle folgenden 'false' Prädikate müssen mit einer sinnvollen Lösung ersetzt werden.

% Wann steht auf einem Feld Gold
gold(X,Y) :- glitter(X, Y).

% Wann ist ein Feld eine sichere Wand
wall(X,Y) :- 
	not(visited(X, Y)), X1 is X-1, Y1 is Y-1, X2 is X+1, Y2 is Y+1,
	((bump(X1, Y), bump(X2, Y));
	(bump(X, Y1),bump(X1, Y));
	(bump(X1, Y),bump(X, Y2));
	(bump(X, Y1),bump(X2, Y));
	(bump(X, Y1),bump(X, Y2));
	(bump(X, Y2),bump(X2, Y))).

% Wann ist ein Feld eine mögliche Wand
possible_wall(X,Y) :-
	X1 is X-1, not(visited(X, Y)), bump(X1, Y);
	X2 is X+1, not(visited(X, Y)), bump(X2, Y);
	Y1 is Y-1, not(visited(X, Y)), bump(X, Y1); 
	Y2 is Y+1, not(visited(X, Y)), bump(X, Y2).
	
% Wann ist ein Feld eine sichere Falle
trap(X,Y) :- 
	not(visited(X, Y)), X1 is X-1, Y1 is Y-1, X2 is X+1, Y2 is Y+1,
	((breeze(X1, Y), breeze(X2, Y));
	(breeze(X, Y1),breeze(X1, Y));
	(breeze(X1, Y),breeze(X, Y2));
	(breeze(X, Y1),breeze(X2, Y));
	(breeze(X, Y1),breeze(X, Y2));
	(breeze(X, Y2),breeze(X2, Y))).

% Wann ist ein Feld eine mögliche Falle
possible_trap(X,Y) :- 
	X1 is X-1, not(visited(X, Y)), breeze(X1, Y);
	X2 is X+1, not(visited(X, Y)), breeze(X2, Y); 
	Y1 is Y-1, not(visited(X, Y)), breeze(X, Y1); 
	Y2 is Y+1, not(visited(X, Y)), breeze(X, Y2).

% Wann ist ein Feld ein möglicher Wumpus
wumpus(X,Y,ID) :- 
	not(visited(X,Y)), not(wall(X,Y)), not(trap(X,Y)),
	stench(X1,Y1,S,ID),  
	XE is abs(X-X1), 
	YE is abs(Y-Y1),
	S is XE+YE,
	(
		(visited(A,B),not(stench(A,B,S2,ID)),AE is abs(X-A),BE is abs(Y-B),S2 is AE+BE, S2=:=3) -> false;
		(visited(A,B),not(stench(A,B,S2,ID)),AE is abs(X-A),BE is abs(Y-B),S2 is AE+BE, S2=:=2) -> false;
		(visited(A,B),not(stench(A,B,S2,ID)),AE is abs(X-A),BE is abs(Y-B),S2 is AE+BE, S2=:=1) -> false; true
	).
	
	






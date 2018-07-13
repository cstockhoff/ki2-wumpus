:- module(karteAusgeben, [weltkarte/0]).
weltkarte() :- mapsize(W,_), weltkarte(W,0).
weltkarte(_,Y) :- mapsize(_,Y), !.
weltkarte(W,Y) :- printRow(W,Y,R,_), printList(R), nl, Y2 is Y+1, weltkarte(W,Y2).

printRow(X,Y,R,_):- X2 is X-1, fillList(0,X2,XList), printRow(XList,Y, R).
printRow([],_,[]).
printRow([K1|R1],Y,[K2|R2]) :- getField(K1,Y,K2), printRow(R1,Y,R2).

getField(X,Y,F) :- gold(X,Y), F is "G".
getField(X,Y,F) :- wumpus(X,Y,_), F is "X".
getField(X,Y,F) :- wall(X,Y), F is "W".
getField(X,Y,F) :- trap(X,Y), F is "T".
getField(_,_,"-").


printList([]).
% Convert number to char if possible
printList([K|R]) :- number(K), name(C,[K]), write(C), printList(R).
% else just print
printList([K|R]) :- write(K), printList(R).

% Create a list filled with the numbers between U and O
fillList(N,N,[N]).
fillList(U,O,[K|R]) :- K is U, U1 is U+1, U =< O, fillList(U1,O,R).

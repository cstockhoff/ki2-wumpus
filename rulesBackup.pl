% Die folgenden 'consults' müssen hier am Anfang stehen!
:- consult('weltkarte').
:- consult('karteAusgeben').

% TODO Alle folgenden 'false' Prädikate müssen mit einer sinnvollen Lösung ersetzt werden.

%>>----------
% Gold
%>>----------

% Wann steht auf einem Feld Gold
gold( X, Y ) :- glitter( X, Y ).

%>>----------
% Wall
%>>----------

% Wann ist ein Feld eine sichere Wand
wall( X, Y ) :-
  not( visited( X, Y ) ),
  (
    doubleTimeBump( X, Y );
    singleTimeBump( X, Y )
  ).

singleTimeBump( X, Y ) :-
  (
    ( XP1 is X + 1, bump( XP1, Y ), XLoc is XP1, YLoc is Y );
    ( XM1 is X - 1, bump( XM1, Y ), XLoc is XM1, YLoc is Y );
    ( YP1 is Y + 1, bump( X, YP1 ), YLoc is YP1, XLoc is X );
    ( YM1 is Y - 1, bump( X, YM1 ), YLoc is YM1, XLoc is X )
  ),
  Count is 0,
  betrachteNachbarfelder( X, Y, XLoc, YLoc, Count, EndCount, [] ),
  EndCount = 3 -> true; false.

betrachteNachbarfelder( X, Y, XLoc, YLoc, Count, EndCount, VList ) :-
  Count < 3,
  AddCount is Count + 1,
  visited( A, B ),
  not( member( visited( A, B ), VList ) ),
  manhatten( A, B, XLoc, YLoc, 1 ),
  betrachteNachbarfelder( X, Y, XLoc, YLoc, AddCount, EndCount, [visited( A, B )|VList] ).

betrachteNachbarfelder( X, Y, XLoc, YLoc, Count, Count, VList ).

doubleTimeBump( X, Y ) :-
  XP1 is X + 1, XM1 is X - 1, YP1 is Y + 1, YM1 is Y - 1,
  (
    ( bump( XM1, Y ), bump( XP1, Y ) );
    ( bump( X, YM1 ), bump( X, YP1 ) );
    ( bump( XM1, Y ), bump( X, YM1 ) );
    ( bump( XP1, Y ), bump( X, YP1 ) );
    ( bump( XM1, Y ), bump( X, YP1 ) );
    ( bump( XP1, Y ), bump( X, YM1 ) )
  ).

% Wann ist ein Feld eine mögliche Wand
possible_wall( X, Y ) :-
  not( visited( X, Y )),
  (
  XP1 is X + 1, bump( XP1, Y );
  XM1 is X - 1, bump( XM1, Y );
  YP1 is Y + 1, bump( X, YP1 );
  YM1 is Y - 1, bump( X, YM1 )
  ).

%>>----------
% Trap
%>>----------

% Wann ist ein Feld eine sichere Falle
trap( X, Y ) :-
  not( visited( X, Y ) ),
  XP1 is X + 1, XM1 is X - 1, YP1 is Y + 1, YM1 is Y - 1,
  (
    ( breeze( XM1, Y ), breeze( XP1, Y ) );
    ( breeze( X, YM1 ), breeze( X, YP1 ) );
    ( breeze( XM1, Y ), breeze( X, YM1 ) );
    ( breeze( XP1, Y ), breeze( X, YP1 ) );
    ( breeze( XM1, Y ), breeze( X, YP1 ) );
    ( breeze( XP1, Y ), breeze( X, YM1 ) )
  ).

% Wann ist ein Feld eine mögliche Falle
possible_trap( X, Y ) :-
  not( visited( X, Y )),
  (
  XP1 is X + 1, breeze( XP1, Y );
  XM1 is X - 1, breeze( XM1, Y );
  YP1 is Y + 1, breeze( X, YP1 );
  YM1 is Y - 1, breeze( X, YM1 )
  ).

%>>----------
% Wumpus
%>>----------

% Wann ist ein Feld ein möglicher Wumpus
wumpus( X, Y, ID ) :-
  fieldWumpusRelevant( X, Y, ID ),
  findAllStenchFields( X, Y, ID, [], [], EndList ), !,
  check4AllStenchFields( X, Y, ID, EndList, [] ),
  (
    (visited( A, B ), not( stench( A, B, 3, ID )), manhatten( X, Y, A, B, 3 )) -> false;
    (visited( A, B ), not( stench( A, B, 2, ID )), manhatten( X, Y, A, B, 2 )) -> false;
    (visited( A, B ), not( stench( A, B, 1, ID )), manhatten( X, Y, A, B, 1 )) -> false; true
  ).

findAllStenchFields( X, Y, ID, VList, List, EndList ) :-
	visited( A, B ), stench( A, B, D, ID ),
	not( member( visited( A, B ), VList ) ),
	findAllStenchFields( X, Y, ID, [visited(A,B)|VList], [D|List], EndList).
/*
findAllStenchFields( X, Y, ID, List, EndList ) :-
	visited( A, B ), stench( A, B, D, ID ),
	not( member( D, List ) ),
	findAllStenchFields( X, Y, ID, [D|List], EndList).
*/

findAllStenchFields( X, Y, ID, VList, List, List ).

check4AllStenchFields( X, Y, ID, [T|List], VList ) :-
  visited( A, B ), stench( A, B, T, ID ),
  not( member( visited( A, B ), VList ) ),
  manhatten( A, B, X, Y, T ),
  check4AllStenchFields( X, Y, ID, List, [visited( A, B )|VList] ).

check4AllStenchFields( X, Y, ID, [], VList ).

fieldWumpusRelevant( X, Y, ID ) :-
  not( visited( X,Y ) ),
  (
    wumpusStenched( X, Y, 1, ID );
    wumpusStenched( X, Y, 2, ID );
    wumpusStenched( X, Y, 3, ID )
  ).

wumpusStenched( X, Y, Distance, ID) :-
  visited( A, B ), stench( A, B, Distance, ID ), manhatten( A, B, X, Y, Distance ).

manhatten( X1, Y1, X2, Y2, Distance ) :-
  S1 is abs(X1 - X2),
  S2 is abs(Y1 - Y2),
  S1 + S2 =:= Distance.

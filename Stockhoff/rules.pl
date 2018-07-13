% Die folgenden 'consults' müssen hier am Anfang stehen!
:- consult('weltkarte').
:- consult('karteAusgeben').

%>>----------
% Gold
%>>----------

% Wann steht auf einem Feld Gold
% Wenn glitter auf dem Feld ist, dann schnappe zu und goenn dir das Gold!
gold( X, Y ) :- glitter( X, Y ).

%>>----------
% Wall
%>>----------

% Wann ist ein Feld eine sichere Wand
%=> Vorgehensweise:
% -> Feld darf nicht besucht sein
% -> Von jedem Nachbarfeld wurde ein Bump wahrgenommen
% -> Entweder
%   1. Wurde von zwei verschiedenen Nachbarfeldern ein Bump wahrgenommen
%   2. Es wurde nur ein Bump gespuert,
%      aber alle Nachbarfeld vom Bumpfeld wurden besucht
wall( X, Y ) :-
  not( visited( X, Y ) ),
  nachbarOhneBump( X, Y ),
  (
    doubleTimeBump( X, Y );
    singleTimeBump( X, Y )
  ).

% Wann ist ein Feld eine mögliche Wand
%=> Kriterien
% -> Feld darf nicht besucht sein
% -> Es wurde von einem Nachbarfeld ein bump gespuert
% -> Von jedem Nachbarfeld wurde ein Bump wahrgenommen
possible_wall( X, Y ) :-
  not( visited( X, Y )),
  (
  XP1 is X + 1, bump( XP1, Y );
  XM1 is X - 1, bump( XM1, Y );
  YP1 is Y + 1, bump( X, YP1 );
  YM1 is Y - 1, bump( X, YM1 )
  ),
  nachbarOhneBump( X, Y ).

%>>-- Auslagerungen
% Wenn von zwei verschiedenen Nachbarfeldern ein Bump gespuert wurde,
% dann ist mit sehr hoher Wahrscheinlichkeit das Feld eine Wand
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

% Wenn nur von einem Feld ein Bump gespuert wurde, aber alle Nachbarfelder
% von dem gespuerten Feld besucht wurden, dann kann das Feld eine Wand
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

% Pruefen, ob ein Nachbarfeld existiert, wo kein Bump gespuert wurde
% Wenn ein solches Feld existiert, dann false, sonst true
nachbarOhneBump( X, Y ) :-
  (
    visited( A, B ),
    not( bump( A, B ) ),
    manhatten( A, B, X, Y, 1 )
  ) ->
    false;
    true.

%>>----------
% Trap
%>>----------
% Wann ist ein Feld eine sichere Falle
% Verhält sich wie wall( X, Y )
trap( X, Y ) :-
  not( visited( X, Y ) ),
  nachbarOhneBreeze( X, Y ),
  (
    doubleTimeBreeze( X, Y );
    singleTimeBreeze( X, Y )
  ).

% Wann ist ein Feld eine mögliche Falle
% Verhält sich wie possible_wall( X, Y )
possible_trap( X, Y ) :-
  not( visited( X, Y )),
  (
  XP1 is X + 1, breeze( XP1, Y );
  XM1 is X - 1, breeze( XM1, Y );
  YP1 is Y + 1, breeze( X, YP1 );
  YM1 is Y - 1, breeze( X, YM1 )
  ),
  nachbarOhneBreeze( X, Y ).

%>>-- Auslagerungen
% Verhält sich wie doubleTimeBump( X, Y )
doubleTimeBreeze( X, Y ) :-
  XP1 is X + 1, XM1 is X - 1, YP1 is Y + 1, YM1 is Y - 1,
  (
    ( breeze( XM1, Y ), breeze( XP1, Y ) );
    ( breeze( X, YM1 ), breeze( X, YP1 ) );
    ( breeze( XM1, Y ), breeze( X, YM1 ) );
    ( breeze( XP1, Y ), breeze( X, YP1 ) );
    ( breeze( XM1, Y ), breeze( X, YP1 ) );
    ( breeze( XP1, Y ), breeze( X, YM1 ) )
  ).

% Verhält sich wie singleTimeBump( X, Y )
singleTimeBreeze( X, Y ) :-
  (
    ( XP1 is X + 1, breeze( XP1, Y ), XLoc is XP1, YLoc is Y );
    ( XM1 is X - 1, breeze( XM1, Y ), XLoc is XM1, YLoc is Y );
    ( YP1 is Y + 1, breeze( X, YP1 ), YLoc is YP1, XLoc is X );
    ( YM1 is Y - 1, breeze( X, YM1 ), YLoc is YM1, XLoc is X )
  ),
  Count is 0,
  betrachteNachbarfelder( X, Y, XLoc, YLoc, Count, EndCount, [] ),
  EndCount = 3 -> true; false.

% Verhält sich wie nachbarOhneBump
nachbarOhneBreeze( X, Y ) :-
  (
  visited( A, B ),
  not( breeze( A, B ) ),
  manhatten( A, B, X, Y, 1 )
  ) ->
    false;
    true.

%>>----------
% Wumpus
%>>----------

% Wann ist ein Feld ein möglicher Wumpus
%=> Vorgehensweise:
% -> Wurde das Feld von einem anderen Feld ueberhaupt mal gerochen
% -> Ermittle alle Stench-Felder anhand der Wumpus-ID und speichere in einer Liste
% -> Anhand der Liste schaue nun, ob von jedem Stench-Feld die aktuelle Position
%    erreicht werden kann
% -> Es darf kein Feld in Riech-Reichweite existieren,
%    wo der Wumpus nicht gerochen wurde
wumpus( X, Y, ID ) :-
  fieldWumpusRelevant( X, Y, ID ),
  findAllStenchFields( X, Y, ID, [], [], EndList ), !,
  check4AllStenchFields( X, Y, ID, EndList, [] ),
  (
    (visited( A, B ), not( stench( A, B, 3, ID )), manhatten( X, Y, A, B, 3 )) -> false;
    (visited( A, B ), not( stench( A, B, 2, ID )), manhatten( X, Y, A, B, 2 )) -> false;
    (visited( A, B ), not( stench( A, B, 1, ID )), manhatten( X, Y, A, B, 1 )) -> false; true
  ).

%>>-- Auslagerungen
% Das Feld darf nicht besucht sein und pruefe, ob ueberhaupt ein Feld
% in Riech-Reichweite existiert, von dem mal was gerochen wurde
fieldWumpusRelevant( X, Y, ID ) :-
  not( visited( X,Y ) ),
  (
    wumpusStenched( X, Y, 1, ID );
    wumpusStenched( X, Y, 2, ID );
    wumpusStenched( X, Y, 3, ID )
  ).

wumpusStenched( X, Y, Distance, ID) :-
  visited( A, B ), stench( A, B, Distance, ID ), manhatten( A, B, X, Y, Distance ).

% Ermittle alle Stench-Felder mit Bezug auf die Wumpus-ID
% Speichere die Distanz in einer Liste
% (Jede Distanz von jedem Stench-Feld wird abgespeichert)
findAllStenchFields( X, Y, ID, VList, List, EndList ) :-
	visited( A, B ), stench( A, B, D, ID ),
	not( member( visited( A, B ), VList ) ),
	findAllStenchFields( X, Y, ID, [visited(A,B)|VList], [D|List], EndList).

findAllStenchFields( X, Y, ID, VList, List, List ).

% Betrachte nun von jedem Stench-Feld aus, ob mit der Distanz aus der vorher
% aufgebauten Liste von dem existierenden Stench-Feld das aktuell zu ueberpruefende
% Feld exakt erreicht werden kann
check4AllStenchFields( X, Y, ID, [T|List], VList ) :-
  visited( A, B ), stench( A, B, T, ID ),
  not( member( visited( A, B ), VList ) ),
  manhatten( A, B, X, Y, T ),
  check4AllStenchFields( X, Y, ID, List, [visited( A, B )|VList] ).

check4AllStenchFields( X, Y, ID, [], VList ).

%>>----------
% Sonstiges
%>>----------
% Prueft, ob die Distanz zwischen zwei Feldern mit der uebergebenen Distanz uebereinstimmt
manhatten( X1, Y1, X2, Y2, Distance ) :-
  S1 is abs(X1 - X2),
  S2 is abs(Y1 - Y2),
  S1 + S2 =:= Distance.

% Zaehlt die Nachbarfelder, die besucht wurden, und speichert das Ergebnis in EndCount
betrachteNachbarfelder( X, Y, XLoc, YLoc, Count, EndCount, VList ) :-
  Count < 3,
  AddCount is Count + 1,
  visited( A, B ),
  not( member( visited( A, B ), VList ) ),
  manhatten( A, B, XLoc, YLoc, 1 ),
  betrachteNachbarfelder( X, Y, XLoc, YLoc, AddCount, EndCount, [visited( A, B )|VList] ).

betrachteNachbarfelder( X, Y, XLoc, YLoc, Count, Count, VList ).

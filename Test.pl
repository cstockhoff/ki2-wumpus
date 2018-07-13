printFields( X, Y, ID, [] ).

printFields( X, Y, ID, [T|List] ) :-
  write('-->'), write( T ), write( '=>' ), write( List ), writeln( '' ),
  printFields( X, Y, ID, List ).
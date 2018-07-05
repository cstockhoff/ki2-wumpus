% Deactivate warnings for facts that are discontigous
:- discontiguous visited/2.
:- discontiguous bump/2.
:- discontiguous breeze/2.
:- discontiguous glitter/2.
:- discontiguous hunter/3.
:- discontiguous stench/4.

max_stench_distance(3).

% mapsize(Width,Height).
mapsize(15,15).


% Facts:
% visited("X,Y")
% glitter("X,Y")
% breeze("X,Y")
% bump("X,Y").
% hunter("X,Y").
% stench("X,Y,Intensity,WumpusID").


bump(1,1).
bump(1,2).
bump(1,3).
bump(1,4).
bump(1,5).
bump(1,7).
bump(1,8).
bump(1,9).
bump(1,10).
bump(1,11).
bump(1,1).
visited(1,1).
visited(1,2).
visited(1,3).
visited(1,4).
visited(1,5).
bump(1,5).
bump(1,7).
visited(1,7).
visited(1,8).
visited(1,9).
visited(1,10).
visited(1,11).
breeze(1,11).
bump(2,1).
visited(2,1).
visited(2,2).
visited(2,3).
visited(2,4).
visited(2,5).
bump(2,5).
bump(3,6).
bump(2,7).
visited(2,7).
visited(2,8).
visited(2,9).
visited(2,10).
visited(2,11).
breeze(2,1).
breeze(4,1).
breeze(3,2).
visited(3,2).
visited(3,3).
visited(3,4).
visited(3,5).
visited(3,6).
visited(3,7).
visited(3,8).
visited(3,9).
bump(2,10).
bump(3,9).
bump(3,11).
visited(3,11).
bump(4,1).
visited(4,1).
visited(4,2).
visited(4,3).
visited(4,4).
visited(4,5).
visited(4,6).
visited(4,7).
visited(4,8).
visited(4,9).
bump(4,9).
bump(4,11).
visited(4,11).
breeze(4,11).
bump(5,1).
visited(5,1).
visited(5,2).
breeze(4,3).
breeze(5,2).
breeze(6,3).
breeze(5,4).
visited(5,4).
visited(5,5).
visited(5,6).
visited(5,7).
visited(5,8).
visited(5,9).
bump(5,9).
bump(6,1).
visited(6,1).
visited(6,2).
visited(6,3).
visited(6,4).
visited(6,5).
bump(5,6).
bump(6,5).
bump(6,7).
visited(6,7).
visited(6,8).
visited(6,9).
bump(7,1).
visited(7,1).
visited(7,2).
visited(7,3).
visited(7,4).
visited(7,5).
bump(7,5).
breeze(6,7).
visited(7,9).
visited(7,10).
bump(7,1).
bump(9,1).
bump(7,2).
bump(9,2).
bump(7,3).
bump(9,3).
bump(7,4).
bump(9,4).
bump(7,5).
visited(8,10).
bump(9,1).
visited(9,1).
visited(9,2).
visited(9,3).
visited(9,4).
bump(10,1).
visited(10,1).
visited(10,2).
bump(9,3).
bump(10,2).
bump(10,4).
visited(10,4).
stench(8,10,3,0).
bump(11,1).
visited(11,1).
glitter(11,2).
visited(11,2).
bump(11,4).
visited(11,4).
bump(12,1).
visited(12,1).
visited(12,2).
bump(12,2).
bump(13,3).
bump(12,4).
visited(12,4).
bump(13,1).
visited(13,1).
visited(13,2).
visited(13,3).
visited(13,4).
bump(13,1).
bump(13,2).
bump(13,3).
bump(13,4).

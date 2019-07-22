#!/usr/bin/bash

javac -Xlint Bag.java
javac -Xlint Graph.java
javac -Xlint GraphGenerator.java
javac -Xlint In.java
javac -XlintMinPQ.java
javac -Xlint Population.java
javac -Xlint SET.java
javac -Xlint Stack.java
javac -Xlint StdIn.java
javac -Xlint StdOut.java
javac -Xlint StdRandom.java

for N in 10 20 25 64 100 225 400 1225 2500 
do
  for I in $(( N / 5 )) $(( N / 2 )) $(( N / 10 ))
  do
    for pn in 0.016 0.1 0.2 0.5 0.7
    do
		  for T in 0.1 0.2 0.3 0.5 0.7 0.9
			do
				for q in 5 10 15
				do
					for F in 5 10 15
					do 
						folder='Outputs/N'$N'I'$I'pn'$pn'T'$T'M10000q'$q'F'$F'/'
						echo "N$N,I$I,pn$pn,T$T,M10000,q$q,F$F"
						rm -rf $folder
						mkdir -p $folder
						java Population $N $I $pn $T 10000 $q $F $folder >> stats.txt
					done
				done
			done
    done
  done
done

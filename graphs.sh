#!/usr/bin/bash

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
						python -W ignore ../plots.py $N $I $pn $T $M $q $F 0
            folder='Outputs/N'$N'I'$I'pn'$pn'T'$T'M10000q'$q'F'$F'/'
            last=$(ls -v1 $folder*.csv | tail -n 1)
            #echo $last
            last=${last::-4}
            discard=$(expr length $folder)
            all=$(expr length $last)
            tale_size=$((all-discard))
            #echo $tale_size
            last=${last:$((-1*tale_size))}
            #echo $last
            python -W ignore ../plots.py $N $I $pn $T 10000 $q $F $last
            #echo "N$N,I$I,pn$pn,T$T,M$M,q$q,F$F"
					done
				done
			done
    done
  done
done

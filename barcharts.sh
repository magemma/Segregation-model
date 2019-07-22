#!/usr/bin/bash
# USAGE: change comment placements in order to obtain barcharts fixing
# any possible parameter

for N in 10 20 25 64 100 225 400 1225 2500 
do
  for I in $(( N / 10 )) $(( N / 5 )) $(( N / 2 )) 
  do
#    for pn in 0.016 0.1 0.2 0.5 0.7
#    do
		  for T in 0.1 0.2 0.3 0.5 0.7 0.9
			do
				for q in 5 10 15
				do
					for F in 5 10 15
					do 
						python barcharts.py $N $I ? $T $q $F
						
					done
				done
			done
    done
#	done
done

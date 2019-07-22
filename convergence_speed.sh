#!/usr/bin/bash

for N in 10 20 25 64 100 225 400 1225 2500 
do
  for I in $(( N / 10 )) $(( N / 5 )) $(( N / 2 )) 
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
						if [ -d "$folder" ]; then
  						# Control will enter here if $DIRECTORY exists.
		          last=$(ls -v1 $folder*.csv | tail -n 1)
		          #echo $last
		          last=${last::-4}
		          discard=$(expr length $folder)
		          all=$(expr length $last)
		          tale_size=$((all-discard))
		          #echo $tale_size
		          last=${last:$((-1*tale_size))}
		          #echo $last
		          echo "$N,$I,$pn,$T,$q,$F,$last" >> output.csv
            fi
					done
				done
			done
    done
  done
done

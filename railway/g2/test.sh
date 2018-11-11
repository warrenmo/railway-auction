#!/usr/bin/env bash

cd ../../

make compile
printf "\nSuccessful make!\nResults:\n\n"


java railway.sim.Simulator \
    -p g2 g5 g6 g7 g8 \
    -m g4 \

    #-p g2 \

cmake_minimum_required(VERSION 3.6)
project(mp03)

add_executable(mp03 tsh.c)
set_property(TARGET mp03 PROPERTY C_STANDARD 90)

add_executable(myspin myspin.c)
set_property(TARGET myspin PROPERTY C_STANDARD 90)

add_executable(myint myint.c)
set_property(TARGET myint PROPERTY C_STANDARD 90)

add_executable(mysplit mysplit.c)
set_property(TARGET mysplit PROPERTY C_STANDARD 90)

add_executable(mystop mystop.c)
set_property(TARGET mystop PROPERTY C_STANDARD 90)

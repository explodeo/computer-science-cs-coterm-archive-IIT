cmake_minimum_required(VERSION 3.6)
project(mp02)

set(SOURCE_FILES
        main.c
        hashtable.c
        hashtable.h)

add_executable(mp02 ${SOURCE_FILES})

set_property(TARGET mp02 PROPERTY C_STANDARD 90)
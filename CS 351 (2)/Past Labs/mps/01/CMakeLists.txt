cmake_minimum_required(VERSION 3.6)
project(mp01)

set(SOURCE_FILES
        hello.c
        hello.h
        main.c)

add_executable(mp01 ${SOURCE_FILES})

set_property(TARGET mp01 PROPERTY C_STANDARD 90)
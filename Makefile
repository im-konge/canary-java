include Makefile.docker

VERSION ?= latest
PROJECT_NAME ?= canary

build: copy_files docker_build docker_tag docker_push clear

copy_files:
	cp canary/src/main/resources/log4j2.properties docker-image/tmp/
	cp canary/target/canary-${VERSION}.jar docker-image/tmp/

clear:
	rm docker-image/tmp/*

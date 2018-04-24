#!/bin/bash

IMAGE_NAME='beanpoll-wildfly-dev'
CONTAINER_NAME="$IMAGE_NAME"

# Create the image if "build" is specified, or it doesn't exist
if [ "$1" == "build" ] || \
	[ "$(docker images -q $IMAGE_NAME 2> /dev/null)" == "" ]; then

	printf '%s\n' "Building $IMAGE_NAME..."

	# Get the MySQL connector if it doesn't exist already
	if [ ! -e mysql-connector-java-5.1.45-bin.jar ]; then
		if [ ! -e mysql-connector-java-5.1.45.tar.gz ]; then
			printf '%s\n' " => Downloading the MySQL Connector for Java... "
			curl -o mysql-connector-java-5.1.45.tar.gz \
				-L https://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-java-5.1.45.tar.gz
		fi
		printf '%s\n' " => Expanding the MySQL Connector for Java... "
		tar xf mysql-connector-java-5.1.45.tar.gz
		mv mysql-connector-java-5.1.45/*.jar .
		rm -rf mysql-connector-java-5.1.45
	fi

	# Build the image
	docker build --no-cache --force-rm -t "$IMAGE_NAME" .    
fi

# Create the container if "build" is specified, or it doesn't exist
if [ "$1" == "new" ] || [ "$1" == "build" ] || \
	! docker ps -a | grep -q "$CONTAINER_NAME"; then

	printf '%s\n' "Running $CONTAINER_NAME from $IMAGE_NAME..."

	# Delete the container, if it's already made
	docker stop "$CONTAINER_NAME" > /dev/null 2>&1
	docker rm "$CONTAINER_NAME" > /dev/null 2>&1

	# Run the container, forwarding ports 80 (to 8080) and 81 (to 9990)
	docker run --name="$CONTAINER_NAME" \
		-p 82:8080 \
		-p 83:9990 \
		-d "$IMAGE_NAME"

else
	# Start the existing container
	docker start "$CONTAINER_NAME"
fi

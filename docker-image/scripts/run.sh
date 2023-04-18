#!/bin/bash
set +x

if [ -z "$JAVA_OPTS" ]; then
    export JAVA_OPTS="${JAVA_OPTS} -Dlog4j2.configurationFile=file:bin/log4j2.properties"
fi

function get_heap_size {
  # Get the max heap used by a jvm which used all the ram available to the container
  CONTAINER_MEMORY_IN_BYTES=$(java -XshowSettings:vm -version \
    |& awk '/Max\. Heap Size \(Estimated\): [0-9KMG]+/{ print $5}' \
    | gawk -f "${MYPATH}"/to_bytes.gawk)

  # use max of 31G memory, java performs much better with Compressed Ordinary Object Pointers
  DEFAULT_MEMORY_CEILING=$((31 * 2**30))
  if [ "${CONTAINER_MEMORY_IN_BYTES}" -lt "${DEFAULT_MEMORY_CEILING}" ]; then
    if [ -z $CONTAINER_HEAP_PERCENT ]; then
      CONTAINER_HEAP_PERCENT=0.50
    fi

    CONTAINER_MEMORY_IN_MB=$((${CONTAINER_MEMORY_IN_BYTES}/1024**2))
    CONTAINER_HEAP_MAX=$(echo "${CONTAINER_MEMORY_IN_MB} ${CONTAINER_HEAP_PERCENT}" | awk '{ printf "%d", $1 * $2 }')

    echo "${CONTAINER_HEAP_MAX}"
  fi
}

MAX_HEAP=$(get_heap_size)
if [ -n "$MAX_HEAP" ]; then
  echo "Configuring Java heap: -Xms${MAX_HEAP}m -Xmx${MAX_HEAP}m"
  export JAVA_OPTS="-Xms${MAX_HEAP}m -Xmx${MAX_HEAP}m $JAVA_OPTS"
fi

# Make sure that we use /dev/urandom
JAVA_OPTS="${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom"

# Enable GC logging for memory tracking
JAVA_OPTS="${JAVA_OPTS} -Xlog:gc*:stdout:time -XX:NativeMemoryTracking=summary"

exec java $JAVA_OPTS -jar $JAR "$@"
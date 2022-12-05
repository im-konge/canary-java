package services.topic;

import java.util.Map;

public record Topic(String topicName, Map<String, String> topicConfig) {}

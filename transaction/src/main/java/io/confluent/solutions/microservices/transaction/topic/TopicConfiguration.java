package io.confluent.solutions.microservices.transaction.topic;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:topics-defaults.properties")
public abstract class TopicConfiguration {
	private String name;
	private boolean compacted = false;
	private int partitions = 1;
	private short replicationFactor = 1;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isCompacted() {
		return compacted;
	}

	public void setCompacted(boolean compacted) {
		this.compacted = compacted;
	}

	public int getPartitions() {
		return partitions;
	}

	public void setPartitions(int partitions) {
		this.partitions = partitions;
	}

	public short getReplicationFactor() {
		return replicationFactor;
	}

	public void setReplicationFactor(short replicationFactor) {
		this.replicationFactor = replicationFactor;
	}

}

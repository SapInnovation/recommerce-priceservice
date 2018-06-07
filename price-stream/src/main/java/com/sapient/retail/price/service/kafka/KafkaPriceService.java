package com.sapient.retail.price.service.kafka;

import java.util.Properties;

import org.I0Itec.zkclient.ZkClient;
import kafka.utils.ZKStringSerializer$;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sapient.retail.price.service.kafka.KafkaConfiguration;

import kafka.admin.AdminUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sapient.retail.price.common.model.Price;

@Service
public class KafkaPriceService {

	@Value (value = "${kafka.intopic}")
	private String priceKafkaInTopic;
	
	@Value(value = "${kafka.server:127.0.0.0:9092}")
	private String kafkaBrokerHosts;
	
	@Value(value = "${kafka.zookeeper:127.0.0.0:2181}")
	private String zookeeperHosts;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private final KafkaConfiguration kafkaConfig;

	public KafkaPriceService(KafkaConfiguration kafkaConfig) {
		this.kafkaConfig = kafkaConfig;
	}

	public void sendDataToKafkaTopic(final Price priceData) {
		logger.info("Sending price data {}", priceData);
		final Producer<String, JsonNode> priceProducer = kafkaConfig.getKafkaPriceProducer();
		try {
			JsonNode jsonObj = new ObjectMapper().valueToTree(priceData);
			final ProducerRecord<String, JsonNode> priceRecord = new ProducerRecord<String, JsonNode>(
					priceKafkaInTopic , priceData.getProductId(), jsonObj);
			priceProducer.send(priceRecord, (metadata, exception) -> {
				if (null != metadata) {
					logger.info("Sent price data {}", priceData.getProductId());
				} else {
					logger.error("exception occurred while sending data to topic :: " + exception.getMessage());
				}
			});
		} finally {
			priceProducer.flush();
			priceProducer.close();
		}

	}
	
	public void createTopicInKafka (String topicName) {
        int sessionTimeOut = 10000;
        int connectionTimeOut = 10000;
        logger.info("zookeeperHosts:{}", zookeeperHosts);
        ZkClient zkClient = new ZkClient(zookeeperHosts, sessionTimeOut, connectionTimeOut, ZKStringSerializer$.MODULE$);
        if (!AdminUtils.topicExists(zkClient, topicName)) {
            int replicationFactor = kafkaBrokerHosts.split(",").length;
            AdminUtils.createTopic(zkClient, topicName, 1, replicationFactor, new Properties());
        } else {
            logger.info(topicName +" is available hence no changes are done");
        }
        logger.info("Topic Details:{}", AdminUtils.fetchTopicMetadataFromZk(topicName, zkClient));
        zkClient.close();
	}

}

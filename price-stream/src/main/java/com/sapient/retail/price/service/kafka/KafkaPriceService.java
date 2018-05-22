package com.sapient.retail.price.service.kafka;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sapient.retail.price.service.kafka.KafkaConfiguration;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sapient.retail.price.common.model.Price;

@Service
public class KafkaPriceService {

	@Value (value = "${kafka.intopic}")
	private String priceKafkaInTopic;
	
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
					priceKafkaInTopic , priceData.getSkuId(), jsonObj);
			priceProducer.send(priceRecord, (metadata, exception) -> {
				if (null != metadata) {
					logger.info("Sent price data {}", priceData.getSkuId() + " :: " + priceData.getPrice());
				} else {
					logger.error("exception occurred while sending data to topic :: " + exception.getMessage());
				}
			});
		} finally {
			priceProducer.flush();
			priceProducer.close();
		}

	}

}

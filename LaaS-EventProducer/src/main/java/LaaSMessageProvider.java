import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.Map;
import java.util.List;
import java.sql.Timestamp;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.PartitionInfo;
import java.util.Iterator;

public class LaaSMessageProvider {

	static String brokerList = "localhost:9092";
	static int throughput = 10;
	static String typeMessage = "JSON";
	static String filterprefix = "";

	static Map<String, List<PartitionInfo>> topics;

	private static String RandomTopic() {
		String Topic = new String("");
		int index = (new Random()).nextInt(topics.size());
		Set<String> keys = topics.keySet();
		Iterator<String> it = keys.iterator();
		for (int idx = 0; idx < index; idx++)
			it.next();
		Topic = (String) it.next();
		System.out.println("Topic randomized: " + Topic);
		return Topic;
	}

	private static Message CreateMessage(String m_typeMessage, String topicToSend, Timestamp ts) {
		if (topicToSend.contains("-") == false)
			return (null); // loyaltycardID-at-Shop not conformed

		Message newMessage;
		newMessage = new Message();
		newMessage.setTimeStamp(ts.toString());
		newMessage.setSeqkey(topicToSend + "_" + String.valueOf(((Double) (Math.random() * 10)).intValue()));
		newMessage.setLoyaltyCard_ID(topicToSend.substring(0, topicToSend.indexOf("-")));
		newMessage.setShop(topicToSend.substring(topicToSend.indexOf("-") + 1, topicToSend.length()));

		String[] ProductOptions = {
				// Danone
				"YoPRO Protein Bar",
				"YoPRO Protein Solid Yogurt",
				"YoPRO Protein Liquid Yogurt",
				"OIKOS",
				"Milk",

				// White Label
				"Protein Bar",
				"Protein Solid Yogurt",
				"Protein Liquid Yogurt",
				"Greek Yogurt",
				"Soda",
				"Soda No Sugar",

				// Coca-Cola
				"Coca-Cola",
				"Coca-Cola Zero",

				// Pepsi
				"Pepsi",
				"Pepsi Zero"
		};

		newMessage.setProduct(ProductOptions[new Random().nextInt(ProductOptions.length)]);

		newMessage.setPrice(String.valueOf(
				((Double) (Math.random() * 30)).intValue() + 0.01 * ((Double) (Math.random() * 100)).intValue()));

		String[] suppliers = {
				"Danone", "White Label", "Coca-Cola", "Pepsi"
		};
		newMessage.setSupplier(suppliers[new Random().nextInt(suppliers.length)]);

		if (m_typeMessage.compareTo("JSON") == 0) {

			newMessage.setAsText(
					"{\"Purchase_Event\":{" +
							"\"TimeStamp\":\"" + newMessage.getTimeStamp() + "\"," +
							"\"LoyaltyCard_ID\":\"" + newMessage.getLoyaltyCard_ID() + "\"," +
							"\"Price\":\"" + newMessage.getPrice() + "\"," +
							"\"Product\":\"" + newMessage.getProduct() + "\"," +
							"\"Supplier\":\"" + newMessage.getSupplier() + "\"," +
							"\"Shop\":\"" + newMessage.getShop() + "\"" +
							"}}");
		} else if (m_typeMessage.compareTo("XML") == 0) {
			newMessage.setAsText(
					"<Purchase_Event>" +
							"<TimeStamp>" + newMessage.getTimeStamp() + "</TimeStamp>" +
							"<LoyaltyCard_ID>" + newMessage.getLoyaltyCard_ID() + "</LoyaltyCard_ID>" +
							"<Price>" + newMessage.getPrice() + "</Price>" +
							"<Product>" + newMessage.getProduct() + "</Product>" +
							"<Supplier>" + newMessage.getSupplier() + "</Supplier>" +
							"<Shop>" + newMessage.getShop() + "</Shop>" +
							"</Purchase_Event>");
		} else {
			System.out.println("Type of message not identified.");
			return (null);
		}

		return (newMessage);
	}

	private static void CheckArguments() {
		System.out.println(
				"--broker-list=" + brokerList + "\n" +
						"--throughput=" + throughput + "\n" +
						"--typeMessage=" + typeMessage + "\n" +
						"--filterprefix=" + filterprefix);
	}

	private static boolean VerifyArgs(String[] cabecalho) {
		for (int i = 0; i < cabecalho.length; i = i + 2) {
			if (cabecalho[i].compareTo("--broker-list") == 0)
				brokerList = cabecalho[i + 1];
			else if (cabecalho[i].compareTo("--throughput") == 0)
				throughput = Integer.valueOf(cabecalho[i + 1]).intValue();
			else if (cabecalho[i].compareTo("--typeMessage") == 0)
				typeMessage = cabecalho[i + 1];
			else if (cabecalho[i].compareTo("--filterprefix") == 0)
				filterprefix = cabecalho[i + 1];
			else {
				System.out.println("Bad argument name: " + cabecalho[i]);
				return (false);
			}
		}

		if (brokerList.length() == 0)
			System.out.println("Broker-list argument is mandatory!");
		else
			return (true);

		return (false);
	}

	private static void SendMessage(Message msg, KafkaProducer<String, String> prd, String topicTarget) {
		System.out.println("This is the message to send = " + msg.getAsText());
		String seqkey = new String("");
		seqkey = msg.getSeqkey();
		System.out.println("Sending new message to Kafka, to the topic = " + topicTarget + ", with key=" + seqkey);
		ProducerRecord<String, String> record = new ProducerRecord<>(topicTarget, seqkey, msg.getAsText());
		prd.send(record);
		System.out.println("Sent...");
	}

	private static void CheckTopicsAvailable() {
		/*** check all topics in kafka cluster from JAVA ******/
		Properties props = new Properties();
		props.put("bootstrap.servers", brokerList);
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

		KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);
		topics = consumer.listTopics();
		consumer.close();

// 		to remove: selled_product_by_discount_coupon
// selled_product_by_location
// selled_product_by_loyalty_card_id
// selled_product_by_shop_id
// testTopic
//560987123-ArcoCegoLisbon
//0
		topics.remove("__consumer_offsets");
		topics.remove("selled_product_by_customer_id");
		topics.remove("selled_product_by_discount_coupon");
		topics.remove("selled_product_by_location");
		topics.remove("selled_product_by_loyalty_card_id");
		topics.remove("selled_product_by_shop_id");
		topics.remove("testTopic");
		topics.remove("560987123-ArcoCegoLisbon");
		topics.remove("0");


		System.out.println("Topics discovered: ");
		for (String topicName : topics.keySet())
			System.out.println("Topic = " + topicName);
		/******************************************************/

	}

	public static void main(String[] args) {

		String usage = "\nThe usage of the Message Producer for LaaS 2024, for Enterprise Integration 2024 course, is the following.\n\n"
				+ "LaaSSimulator "
				+ "--broker-list <brokers> "
				+ "--throughput <value> "
				+ "--typeMessage <value> "
				+ "--filterprefix <value> "
				+ "\n"
				+ "where, \n"
				+ "--broker-list: is a broker list with ports (e.g.: kafka02.example.com:9092,kafka03.example.com:9092), default value is localhost:9092\n"
				+ "--throughput: is the approximate maximum messages to be produced by minute, default value is 10\n"
				+ "--typeMessage: is the type of message to be produced: JSON or XML, default value is JSON\n"
				+ "--filterprefix: is the prefix to be filtered. Only the topics starting with this prefix will be considered to sending messages.\n";

		Properties kafkaProps = new Properties();
		if (args.length == 0)
			System.out.println(usage);
		else {
			if (VerifyArgs(args)) {
				System.out.println("The following arguments are accepted:");
				CheckArguments();
				System.out.println("------- Processing starting -------");

				kafkaProps.put("bootstrap.servers", brokerList);
				kafkaProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
				kafkaProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
				KafkaProducer<String, String> producer = new KafkaProducer<String, String>(kafkaProps);

				CheckTopicsAvailable();

				Timestamp mili;

				while (true) {
					try {
						mili = new Timestamp(System.currentTimeMillis());

						if (!topics.isEmpty()) {
							String topic_to_send = RandomTopic();

							if (topic_to_send.startsWith(filterprefix)) {
								Message messageToSend = CreateMessage(typeMessage, topic_to_send, mili);
								if (messageToSend != null)
									SendMessage(messageToSend, producer, topic_to_send);
							} else
								System.out.println("Topic = " + topic_to_send
										+ " has been filtered. Therefore, not sending message.");
						} else
							System.out.println("Empty list of Topics. Therefore, no message to send.");

						Timestamp timestamp = new Timestamp(System.currentTimeMillis());
						System.out.println("Waiting..." + timestamp);
						Thread.sleep(60000 / throughput);
						CheckTopicsAvailable();
					} catch (Exception e) {
						e.printStackTrace();
					}

					System.out.println("Fire-and-forget stopped.");
				}
			} else
				System.out.println("Application Arguments bad usage.\n\nPlease check syntax.\n\n" + usage);
		}

	}

}

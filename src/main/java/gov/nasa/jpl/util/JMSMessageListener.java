/**
 * Example client used to listen to jms messages. 
 * Used for debugging the JMS messages that PMA sends out.
 * @author hang
 */
package gov.nasa.jpl.util;

import javax.jms.*;

import org.apache.activemq.ActiveMQConnectionFactory;

public class JMSMessageListener 
{
	public static void main(String[] args) {
		ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://cae-ems-alf5int.jpl.nasa.gov:61616"); // ActiveMQ-specific (more)
		try {
			Connection con = factory.createConnection();

			try {
				Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE); // non-transacted session (more)

				 Destination destination = session.createTopic("master");
				 
				 MessageConsumer consumer = session.createConsumer(destination);
				 
				con.start(); // start the connection (more)
				while (true) { // run forever
					Message msg = consumer.receive(); // blocking! (more)
					if (!(msg instanceof TextMessage))
						throw new RuntimeException("Expected a TextMessage");
					TextMessage tm = (TextMessage) msg;
					System.out.println("Message: ");
					System.out.println(tm.getText()); // print message content
					System.out.println("To String: "+tm.toString());
					System.out.println();
				}
			} finally {
				con.close(); // free all resources (more)
			}
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

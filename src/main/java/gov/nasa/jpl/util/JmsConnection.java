package gov.nasa.jpl.util;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.imageio.spi.ServiceRegistry;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.ActiveMQConnectionFactory;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import gov.nasa.jpl.mmsUtil.MMSUtil;

/**
 * @author cinyoung
 *
 */
public class JmsConnection {
    private static Logger logger = Logger.getLogger(JmsConnection.class);
    private long sequenceId = 0;
    private String workspace = null;
    private String projectId = null;

    private static ServiceRegistry services;
    private static Map<String, ConnectionInfo> connectionMap = new HashMap<String, ConnectionInfo>();

	public static final String TYPE_BRANCH = "BRANCH";
	public static final String TYPE_COMMIT = "COMMIT";
	public static final String TYPE_DELTA = "DELTA";
	public static final String TYPE_MERGE = "MERGE";
	
	String hostName = "";
	
	protected static Map<String, ConnectionInfo> getConnectionMap() {

		return connectionMap;
	}
    
    
    public enum DestinationType {
        TOPIC, QUEUE
    }

    static class ConnectionInfo {
        public InitialContext ctx = null;
        public String ctxFactory = "org.apache.activemq.jndi.ActiveMQInitialContextFactory";
        public String connFactory = "ConnectionFactory";
        public String username = null;
        public String password = null;
        public String destName = "master";
        public String uri = "tcp://localhost:61616";
        public ConnectionFactory connectionFactory  = new ActiveMQConnectionFactory(uri);
        public DestinationType destType = DestinationType.TOPIC;
    }
    
    protected boolean init(String eventType) {
        ConnectionInfo ci = getConnectionMap().get( eventType );
        if (ci == null) return false;
        
        System.setProperty("weblogic.security.SSL.ignoreHostnameVerification", "true");
        System.setProperty ("jsse.enableSNIExtension", "false");
        Hashtable<String, String> properties = new Hashtable<String, String>();
        properties.put(Context.INITIAL_CONTEXT_FACTORY, ci.ctxFactory);
        properties.put(Context.PROVIDER_URL, ci.uri);
        if (ci.username != null && ci.password != null) {
            properties.put(Context.SECURITY_PRINCIPAL, ci.username);
            properties.put(Context.SECURITY_CREDENTIALS, ci.password);
        }

        try {
            ci.ctx = new InitialContext(properties);
        } catch (NamingException ne) {
            ne.printStackTrace(System.err);
            return false;
        }

        try {
            ci.connectionFactory = (ConnectionFactory) ci.ctx.lookup(ci.connFactory);
        }
        catch (NamingException ne) {
            ne.printStackTrace(System.err);
            return false;
        }
        
        return true;
    }
    
    
    public boolean publish(JSONObject json, String eventType, String workspaceId, String projectId) {
        boolean result = false;
		            this.workspace = workspaceId;
		            this.projectId = projectId;
		            result = publishMessage(json.toString(), eventType);
        
        return result;
    }
    
    protected static ConnectionInfo initConnectionInfo(String eventType) {
        ConnectionInfo ci = new ConnectionInfo();
        if ( connectionMap == null ) {
            connectionMap = new HashMap< String, JmsConnection.ConnectionInfo >();
        }
        connectionMap.put( eventType, ci );
        return ci;
    }
    
    
    public boolean publishMessage(String msg, String eventType) {
    	
    	logger.info("publish event type: "+eventType);
    	
        ConnectionInfo ci = getConnectionMap().get( eventType );
        logger.info("CI URI: "+ci.uri);
        if ( ci.uri == null) return false;

        if (init(eventType) == false) return false;
        
        boolean status = true;
        try {
            // Create a Connection
            Connection connection = ci.connectionFactory.createConnection();
            
            connection.start();

            // Create a Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // lookup the destination
            
            
            Destination destination;
            
            Session sess = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//            Destination dest = sess.createTopic("master");
            destination = sess.createTopic("master");
//            try {
//                destination = (Destination) ci.ctx.lookup( ci.destName );
//            } catch (NameNotFoundException nnfe) {
//                switch (ci.destType) {
//                    case QUEUE:
//                        destination = session.createQueue( ci.destName );
//                        break;
//                    case TOPIC:
//                    default:
//                        destination = session.createTopic( ci.destName );
//                }
//            }

            // Create a MessageProducer from the Session to the Topic or Queue
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);

            // Create a message
            TextMessage message = session.createTextMessage(msg);
            if (workspace != null) {
                message.setStringProperty( "workspace", workspace );
            } else {
                message.setStringProperty( "workspace", "master" );
            }
            if (projectId != null) {
                message.setStringProperty( "projectId", projectId );
            }
            message.setLongProperty( "MessageID", sequenceId++ );
//            message.setStringProperty( "MessageSource", hostName );
//            message.setStringProperty( "MessageRecipient", "TMS" );
            message.setStringProperty( "MessageType", eventType.toUpperCase() );

            logger.info("before send");
            // Tell the producer to send the message
            producer.send(message);
            logger.info("Message sent: "+message);
            // Clean up
            session.close();
            connection.close();
        }
        catch (Exception e) {
    		e.printStackTrace();
    		logger.info("JMS exception caught, probably means JMS broker not up "+e.toString());
            System.out.println( "JMS exception caught, probably means JMS broker not up");
            status = false;
        }
        
        return status;
    }
    
    public void setWorkspace( String workspace ) {
        this.workspace = workspace;
    }

    public void setProjectId( String projectId ) {
        this.projectId = projectId;
    }
    
    public JSONObject toJson() {
    	try
    	{
        JSONArray connections = new JSONArray();

        for (String eventType: getConnectionMap().keySet()) {
            ConnectionInfo ci = getConnectionMap().get( eventType );
            if (ci.uri.contains( "localhost" )) {
                ci.uri = ci.uri.replace("localhost", hostName);
                getConnectionMap().put( eventType, ci );
            }

            JSONObject connJson = new JSONObject();
            connJson.put( "uri", ci.uri );
            connJson.put( "connFactory", ci.connFactory );
            connJson.put( "ctxFactory", ci.ctxFactory );
            connJson.put( "password", ci.password );
            connJson.put( "username", ci.username );
            connJson.put( "destName", ci.destName );
            connJson.put( "destType", ci.destType.toString() );
            connJson.put( "eventType", eventType );
            
            connections.put( connJson );
        }
        
        JSONObject json = new JSONObject();
        json.put( "connections", connections );

        
        return json;
    	
	}
	catch(JSONException e)
	{
		e.printStackTrace();
		logger.info(e.toString());
	}
    	 JSONObject json = new JSONObject();
    	return json;
    }

    /**
     * Handle single and multiple connections embedded as connections array or not
     */
    public void ingestJson(JSONObject json) {
    	try
    	{
	        if (json.has( "connections" )) {
	            JSONArray connections = json.getJSONArray( "connections" );
	            for (int ii = 0; ii < connections.length(); ii++) {
	                JSONObject connection = connections.getJSONObject( ii );
	                ingestConnectionJson(connection);
	            }
	        } else {
	            ingestConnectionJson(json);
	        }
    	}
    	catch(JSONException e)
    	{
    		e.printStackTrace();
    		logger.info(e.toString());
    	}
    }
    
    public void ingestConnectionJson(JSONObject json) {
    	try
    	{
    		logger.info("ingest json");
	        String eventType = null;
	        if (json.has( "eventType" )) {
	            eventType = json.isNull( "eventType" ) ? null : json.getString( "eventType" );
	        }
	        if (eventType == null) {
	            eventType = TYPE_DELTA;
	        }
	        
	        ConnectionInfo ci;
	        if (getConnectionMap().containsKey( eventType )) {
	            ci = getConnectionMap().get( eventType );
	        } else {
	            ci = new ConnectionInfo();
	        }
	        
	        if (json.has( "uri" )) {
//	        	System.out.println("IS NULL: "+json.isNull( "uri" ) );
//	        	System.out.println("JSON URI: "+json.getString( "uri" ));
	            ci.uri = json.isNull( "uri" ) ? null : json.getString( "uri" );
	        }
	        if (json.has( "connFactory" )) {
	            ci.connFactory = json.isNull("connFactory") ? null : json.getString( "connFactory" );
	        }
	        if (json.has( "ctxFactory" )) {
	            ci.ctxFactory = json.isNull("ctxFactory") ? null : json.getString( "ctxFactory" );
	        }
	        if (json.has( "password" )) {
	            ci.password = json.isNull("password") ? null : json.getString( "password" );
	        }
	        if (json.has( "username" )) {
	            ci.username = json.isNull("username") ? null : json.getString( "username" );
	        }
	        if (json.has( "destName" )) {
	            ci.destName = json.isNull( "destName" ) ? null : json.getString( "destName" );
	        }
	        if (json.has( "destType" )) {
	            if (json.isNull( "destType" )) {
	                ci.destType = null;
	            } else {
	                String type = json.getString( "destType" );
	                if (type.equalsIgnoreCase( "topic" )) {
	                    ci.destType = DestinationType.TOPIC;
	                } else if (type.equalsIgnoreCase( "queue" )) {
	                    ci.destType = DestinationType.QUEUE;
	                } else {
	                    ci.destType = DestinationType.TOPIC;
	                }
	            }
	        }
	        logger.info("ingested eventType: "+eventType);
	        logger.info("ingested ci uri: "+ci.uri);
	        logger.info("ingested ci dest: "+ci.destName);
	       	this.connectionMap.put( eventType, ci );
	       	
	    	
		} catch (JSONException e) { 
			e.printStackTrace();
			logger.info(e.toString());
		}
	}

    public void setServices( ServiceRegistry services ) {
        JmsConnection.services = services;
    }

    public static void main(String[] args)
    {
    	try
    	{
    		String mmsServer = "opencae-int.jpl.nasa.gov";
	    	JmsConnection jmc = new JmsConnection();
	    	
	    	String jmsSettings = MMSUtil.getJMSSettings(mmsServer);
	    	JSONObject connectionJson = new JSONObject(jmsSettings);
	    	jmc.ingestJson(connectionJson);
	    	String workspaceID ="master";
	    	String projectID = "Tommy";
	    	
	    	for (Map.Entry entry : jmc.getConnectionMap().entrySet()) {
	    		ConnectionInfo ci = (ConnectionInfo) entry.getValue();
	    		System.out.println("key: "+entry.getKey() + " URL: " + ci.uri);
	    	}
	    	
	    	JSONObject temp = new JSONObject("{\"refId\": \"master\"}");
	    	jmc.publish(temp, TYPE_DELTA, workspaceID, projectID);
    	}
    	catch(JSONException e)
    	{
    		e.printStackTrace();
    		logger.info(e.toString());
    	}
    }
}

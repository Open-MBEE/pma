package gov.nasa.jpl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PmaApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(PmaApplication.class);
		Map<String, Object> map = new HashMap<>();
		
		// If the certificate does not exist, then the server will be http.
		String sslCertificatePath = "/etc/pki/certs/server.jks";
		File f = new File(sslCertificatePath);
		
		if(f.exists() && !f.isDirectory()) 
		{
			String sslKeyPassword = System.getenv("ssl_key_password");
			String sslKeyAlias = System.getenv("ssl_key_alias");
			if((sslKeyAlias!=null)&&(sslKeyPassword!=null))
			{
				System.out.println("sslkeypass: " + sslKeyPassword);
				System.out.println("sslkeyalias: " + sslKeyAlias);
				map.put("server.ssl.key-store", sslCertificatePath);
				map.put("server.ssl.key-store-password", sslKeyPassword);
				map.put("server.ssl.keyAlias", sslKeyAlias);
				System.out.println("STARTING IN SSL");
			}
		}
		map.put("SERVER_PORT", "8443");
		application.setDefaultProperties(map);
		application.run(args);
	}
	
    /**
     * This is for specifying the usage of TLSv1.1 and TLSv1.2. 
     * Without this, TLS1.0 would be used as well, which is a security flaw.
     * @return
     */
	@Bean
	public EmbeddedServletContainerFactory servletContainerFactory()
	{
	    TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();

	    factory.addConnectorCustomizers(new TomcatConnectorCustomizer()
	    {
	        @Override
	        public void customize(Connector connector)
	        {
	            connector.setAttribute("sslProtocols", "TLSv1.1,TLSv1.2");
	            connector.setAttribute("sslEnabledProtocols", "TLSv1.1,TLSv1.2");
	        }
	    });

	    return factory;
	}
    
}

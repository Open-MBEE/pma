package gov.nasa.jpl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
}

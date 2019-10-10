package springexample;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.braintreegateway.BraintreeGateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static String DEFAULT_CONFIG_FILENAME = "config.properties";
    public static BraintreeGateway gateway;
    public static C2Configuration c2Configuration = new C2Configuration();

    public static void main(String[] args) {
        File configFile = new File(DEFAULT_CONFIG_FILENAME);
        try {
            if(configFile.exists() && !configFile.isDirectory()) {
                gateway = BraintreeGatewayFactory.fromConfigFile(configFile);
            } else {
                gateway = BraintreeGatewayFactory.fromConfigMapping(System.getenv());
            }
        } catch (NullPointerException e) {
            System.err.println("Could not load Braintree configuration from config file or system environment.");
            System.exit(1);
        }

        try (InputStream input = new FileInputStream("c2.properties")) {

            Properties prop = new Properties();

            // load a properties file
            prop.load(input);

            c2Configuration.setMerchantSite(prop.getProperty("merchantSite"));
            c2Configuration.setApi(prop.getProperty("api"));
            c2Configuration.setHpp(prop.getProperty("hpp"));
            c2Configuration.setMerchantAlias(prop.getProperty("merchantAlias"));
            c2Configuration.setSecretKey(prop.getProperty("secretKey"));
            c2Configuration.setPublicApiKey(prop.getProperty("publicApiKey"));

        } catch (IOException ex) {
            System.err.println("Could not load configuration from c2.properties.");
            System.exit(1);
        }

        SpringApplication.run(Application.class, args);
    }
}

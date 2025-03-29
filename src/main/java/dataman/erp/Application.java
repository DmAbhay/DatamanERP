package dataman.erp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@SpringBootApplication
@ComponentScan(basePackages = {"dataman.erp", "dataman.dmbase.redissessionutil", "dataman.dmbase.customconfig"})
public class Application {

	public static void main(String[] args) {

		SpringApplication.run(Application.class, args);

		String input = "SA1";

		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));

			// Convert byte array to Hex String
			StringBuilder hexString = new StringBuilder();
			for (byte b : hashBytes) {
				hexString.append(String.format("%02X", b)); // Converts to uppercase HEX
			}
			System.out.println(hexString.toString());
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("MD5 Algorithm not found", e);
		}
	}

}

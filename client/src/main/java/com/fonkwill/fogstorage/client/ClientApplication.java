package com.fonkwill.fogstorage.client;

import com.fonkwill.fogstorage.client.configuration.ApplicationProperties;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;



@SpringBootApplication
@EnableConfigurationProperties({ApplicationProperties.class})
public class ClientApplication implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(ClientApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ClientApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

        Options options = new Options();
        Option upload = new Option("u", true, "Upload file");
        options.addOption("u", true, "Upload file");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption(upload.getOpt())) {
            String file = cmd.getOptionValue(upload.getOpt());

            System.out.println(file);
        } else {
            System.out.println(false);
        }
	}
}

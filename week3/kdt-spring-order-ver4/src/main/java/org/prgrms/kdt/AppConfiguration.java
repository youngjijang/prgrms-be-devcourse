package org.prgrms.kdt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Optional;
import java.util.UUID;

@Configuration
@ComponentScan
@PropertySource("application.properties")
public class AppConfiguration {

}

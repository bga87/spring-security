package org.example.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = {"org.example.dao", "org.example.services"})
@Import(PersistenceConfig.class)
public class RootConfig {

}
package cz.tul.beran.weather.config;

import cz.tul.beran.weather.service.api.OpenWeatherApi;
import cz.tul.beran.weather.service.api.WeatherProvider;
import org.slf4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiConfig {

  @Bean
  public WeatherProvider weatherProvider(Logger logger) {
    return new OpenWeatherApi(logger);
  }
}

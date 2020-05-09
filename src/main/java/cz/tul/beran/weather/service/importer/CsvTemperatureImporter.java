package cz.tul.beran.weather.service.importer;

import cz.tul.beran.weather.dto.importer.CsvWeatherRow;
import cz.tul.beran.weather.entity.mongo.Temperature;
import cz.tul.beran.weather.repository.mysql.CityRepository;
import cz.tul.beran.weather.repository.mysql.CountryRepository;
import cz.tul.beran.weather.service.mongo.TemperatureService;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Component
public class CsvTemperatureImporter implements TemperatureImporter {

  private final CountryRepository countryRepository;
  private final CityRepository cityRepository;
  private final TemperatureService temperatureService;

  public CsvTemperatureImporter(
      CountryRepository countryRepository,
      CityRepository cityRepository,
      TemperatureService temperatureService) {
    this.countryRepository = countryRepository;
    this.cityRepository = cityRepository;
    this.temperatureService = temperatureService;
  }

  @Override
  public void importTemperatures(MultipartFile multipartFile) {
    if (null == multipartFile) {
      throw new IllegalArgumentException("Imported csv can not be null");
    }

    try {
      String contents = new String(multipartFile.getBytes());
      List<CsvWeatherRow> rows = parseCsv(contents);

      for (CsvWeatherRow row : rows) {
        if (null == countryRepository.findByCodeEquals(row.getCountryCode())) {
          continue;
        }

        if (null == cityRepository.findByNameEquals(row.getCityName())) {
          continue;
        }

        temperatureService.create(Temperature.createFromCsvRow(row));
      }

    } catch (IOException | ParseException e) {
      throw new IllegalArgumentException("Imported csv is not valid");
    }
  }

  private List<CsvWeatherRow> parseCsv(String contents) throws ParseException {

    String[] rows = contents.split(System.getProperty("line.separator"));
    List<CsvWeatherRow> result = new ArrayList<>();

    for (String row : rows) {

      String[] rowParts = row.split(COMMA_DELIMITER);
      if (rowParts.length == EXPECTED_COLUMNS) {
        result.add(new CsvWeatherRow(rowParts));
      }
    }

    return result;
  }
}

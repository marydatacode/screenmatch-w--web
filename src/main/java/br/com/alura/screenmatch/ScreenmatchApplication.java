package br.com.alura.screenmatch;

import br.com.alura.screenmatch.model.DataSerie;
import br.com.alura.screenmatch.service.ConsumptionAPI;
import br.com.alura.screenmatch.service.ConvertData;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {

	public static void main(String[] args) {

		SpringApplication.run(ScreenmatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		//ConsumptionAPI consumptionAPI =  new ConsumptionAPI();
		var consumptionAPI =  new ConsumptionAPI();
		var json = consumptionAPI.obterDados("https://www.omdbapi.com/?t=gilmore+girls&apikey=305ca3f3");
		System.out.println(json);
		ConvertData convert = new ConvertData();
		DataSerie data = convert.getData(json, DataSerie.class);
		System.out.println(data);
	}
}

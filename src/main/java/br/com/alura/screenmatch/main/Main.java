package br.com.alura.screenmatch.main;

import br.com.alura.screenmatch.model.DataEpisode;
import br.com.alura.screenmatch.model.DataSeason;
import br.com.alura.screenmatch.model.DataSerie;
import br.com.alura.screenmatch.model.Episode;
import br.com.alura.screenmatch.service.ConsumptionAPI;
import br.com.alura.screenmatch.service.ConvertData;
import br.com.alura.screenmatch.service.IConvertData;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    private Scanner reading = new Scanner(System.in);

    private ConsumptionAPI consumptionAPI = new ConsumptionAPI();

    private ConvertData convert = new ConvertData();

    private final String ADDRESS ="https://www.omdbapi.com/?t=";

    private final String API_KEY = "&apikey=305ca3f3";

    public void displayMenu(){
        System.out.println("Type the name of the series to search");
        var nameSerie = reading.nextLine();
        var json = consumptionAPI.getData(ADDRESS + nameSerie.replace(" ","+") + API_KEY);
        DataSerie data = convert.getData(json, DataSerie.class);
        System.out.println(data);

        List<DataSeason> seasons = new ArrayList<>();

		for (int i = 1; i <= data.totalSeasons(); i++){
			json = consumptionAPI.getData(ADDRESS + nameSerie.replace(" ","+") + "&season=" + i + API_KEY);
			DataSeason dataSeason = convert.getData(json, DataSeason.class);
			seasons.add(dataSeason);
		}
		seasons.forEach(System.out::println);

        //for (int i = 0; i < data.totalSeasons(); i++){
          //  List<DataEpisode> episodesSeasons = number.get(i).episodes();
          //  for (int j = 0; j< episodesSeasons.size(); j++){
          //      System.out.println(episodesSeasons.get(j).title());
          //  }
        // }

        seasons.forEach(s -> s.episodes().forEach(e -> System.out.println(e.title())));

//        List<String> names = Arrays.asList("Eduarda", "Ana", "Maria", "Luiza");
//
//        names.stream()
//                .sorted()
//                .limit(3)
//                .filter(n -> n.startsWith("N"))
//                .map(n -> n.toUpperCase())
//                .forEach(System.out::println);

        List<DataEpisode> dataEpisodes = seasons.stream()
                .flatMap(s -> s.episodes().stream())
                .collect(Collectors.toList());

        System.out.println("\nTop 10 Episodes");
        dataEpisodes.stream()
                .filter(e -> !e.rating().equalsIgnoreCase("N/A "))
                .peek(e -> System.out.println("Firts Filter(N/A) " + e))
                .sorted(Comparator.comparing(DataEpisode::rating).reversed())
                .peek(e -> System.out.println("Ordenation " + e))
                .limit(10)
                .peek(e -> System.out.println("Limit " + e))
                .map(e -> e.title(). toUpperCase())
                .peek(e -> System.out.println("Mapping " + e))
                .forEach(System.out::println);

        List<Episode> episodes = seasons.stream()
                .flatMap(s -> s.episodes().stream()
                        .map(d -> new Episode(s.number(), d)))
                .collect(Collectors.toList());

        episodes.forEach(System.out::println);

        System.out.println("enter a snippet of the episode title");
        var excerptTitle = reading.nextLine();
        Optional<Episode> episodeSearched = episodes.stream()
                .filter(e -> e.getTitle().toUpperCase().contains(excerptTitle.toUpperCase()))
                .findFirst();
        if (episodeSearched.isPresent()){
            System.out.println("Episode found!");
            System.out.println("Season: " + episodeSearched.get().getSeason());
        } else {
            System.out.println("Episode not found!");
        }

        System.out.println("From what year do you want to watch the episodes?");
        var year = reading.nextInt();
        reading.nextLine();

        LocalDate dateSearch = LocalDate.of(year, 1,1);

        DateTimeFormatter formatter =  DateTimeFormatter.ofPattern("dd/MM/yyyy");

        episodes.stream()
                .filter(e -> e.getReleaseDate() != null && e.getReleaseDate().isAfter(dateSearch))
                .forEach(e -> System.out.println(
                        "Season: " + e.getSeason() +
                                " Episode: " + e.getTitle() +
                                " Release Date: " + e.getReleaseDate().format(formatter)
                ));

        Map<Integer, Double>  ratingForSeason = episodes.stream()
                .filter(e-> e.getRating() > 0.0)
                .collect(Collectors.groupingBy(Episode::getSeason,
                        Collectors.averagingDouble(Episode::getRating)));
        System.out.println(ratingForSeason);

        DoubleSummaryStatistics sta = episodes.stream()
                .filter(e-> e.getRating() > 0.0)
                .collect(Collectors.summarizingDouble(Episode::getRating));
        System.out.println("Mean: " + sta.getAverage());
        System.out.println("Best episode: " + sta.getMax());
        System.out.println("Worst episode: " + sta.getMin());
        System.out.println("Amount: " + sta.getCount());
    }
}

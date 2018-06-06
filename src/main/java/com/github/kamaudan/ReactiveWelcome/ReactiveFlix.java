package com.github.kamaudan.ReactiveWelcome;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.awt.*;
import java.time.Duration;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class ReactiveFlix {


    @Bean
    CommandLineRunner demo( MovieRepository movieRepository) {
        return  args -> movieRepository.deleteAll()
                .subscribe( null, null ,() ->
                    Stream.of("Aeon Flux", "Enter the Mono<Void>", "The fluxinator", "Silence Of Lambdas",
                            "Reactive Mongos on Plane", "Y Tu Mono Tambien", "Attack of fluxxes", "Back to the future")
                            .map(name -> new Movie(UUID.randomUUID().toString(), name, randomGenre()))
                            .forEach( m -> movieRepository.save(m).subscribe(System.out::println)));

        }

        private  String randomGenre() {
            String [] genre = "Horror, Documentary, Action, Drama, Romcom".split(",");
            return genre[new  Random().nextInt(genre.length)];

        }

        public static void main(String[] args) {
            SpringApplication.run(FlixService.class, args);
        }
}

@Service
class FlixService {

    private MovieRepository movieRepository;


    public Flux<MovieEvents> streamStreams(Movie movie) {

        Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));



        Flux<MovieEvents> events = Flux.fromStream(Stream.generate() -> new MovieEvents(movie, new Date(), randomUsers()));

        return Flux.zip(interval, events).map(x -> x.getT2());

    }


    private String randomUsers() {
        String[] users = " Dan, Carol, Peris, David, Josiah, Ian, Didi".split(",");
        return users[new Random().nextInt(users.length)];


    }

    public Mono<Movie> byId(String id) {
        return movieRepository.findById(id);
    }

    public Flux<Movie> all() {
        return movieRepository.findAll();
    }
}

    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @Data
    class MovieEvents {
        private Movie movie;
        private Date when;
        private String user;
    }


@RestController
@RequestMapping("/movies")
class  MovieRestController {

    FlixService flixService;

    @Autowired
    public  MovieRestController(FlixService flixService){
        this.flixService = flixService;

    }

    @GetMapping("/{id}")
    public Mono<Movie> byId(@PathVariable String id){
        return flixService.byId(id);
    }

    @GetMapping
    public Flux<Movie> all() {
        return flixService.all();

    }

    @GetMapping(value = "/{id}/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<MovieEvents> events (@PathVariable String id){
        return  flixService.byId(id)
                .flatMapMany(movie -> flixService.streamStreams(movie));


    }
}

interface  MovieRepository extends ReactiveMongoRepository<Movie, String> {


}


@Document
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
class Movie {

    @Id
    private  String id;

    private  String title ;

    private String genre;
}
package org.konveyor.springboot.reactive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import java.time.Duration;

@RestController
@RequestMapping("/reactive/heroes")
public class ReactiveHeroController {

    @Autowired
    private ReactiveHeroService heroService;

    private final WebClient webClient;

    public ReactiveHeroController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://external-service").build();
    }

    @GetMapping
    public Flux<Hero> getAllHeroes() {
        return heroService.findAllHeroes();
    }

    @GetMapping("/{id}")
    public Mono<Hero> getHero(@PathVariable Long id) {
        return heroService.findHeroById(id)
                .switchIfEmpty(Mono.error(new HeroNotFoundException(id)));
    }

    @PostMapping
    public Mono<Hero> createHero(@RequestBody Mono<Hero> heroMono) {
        return heroMono
                .flatMap(heroService::saveHero)
                .doOnSuccess(hero -> System.out.println("Created hero: " + hero.getName()));
    }

    @GetMapping("/external/{id}")
    public Mono<Hero> getExternalHero(@PathVariable Long id) {
        return webClient.get()
                .uri("/heroes/{id}", id)
                .retrieve()
                .bodyToMono(Hero.class)
                .timeout(Duration.ofSeconds(5))
                .retry(3);
    }
}

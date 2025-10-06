package org.konveyor.springboot.annotations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.List;

@RestController
@RequestMapping("/heroes")
@CrossOrigin(origins = "http://localhost:3000")
public class HeroController {

    @Autowired
    private HeroService heroService;

    @GetMapping
    public List<Hero> getAllHeroes() {
        return heroService.findAllHeroes();
    }

    @GetMapping("/{id}")
    public Hero getHero(@PathVariable Long id) {
        return heroService.findHeroById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Hero createHero(@RequestBody Hero hero) {
        return heroService.saveHero(hero);
    }

    @PutMapping("/{id}")
    public Hero updateHero(@PathVariable Long id, @RequestBody Hero hero) {
        return heroService.updateHero(id, hero);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHero(@PathVariable Long id) {
        heroService.deleteHero(id);
    }

    @GetMapping("/search")
    public List<Hero> searchHeroes(@RequestParam String name, 
                                   @RequestHeader("X-Request-ID") String requestId) {
        return heroService.findByName(name);
    }
}

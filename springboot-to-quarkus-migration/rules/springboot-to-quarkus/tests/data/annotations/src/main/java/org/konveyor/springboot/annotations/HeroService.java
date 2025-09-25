package org.konveyor.springboot.annotations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class HeroService {

    @Autowired
    private HeroRepository heroRepository;

    public List<Hero> findAllHeroes() {
        return heroRepository.findAll();
    }

    public Hero findHeroById(Long id) {
        return heroRepository.findById(id).orElse(null);
    }

    public Hero saveHero(Hero hero) {
        return heroRepository.save(hero);
    }

    public Hero updateHero(Long id, Hero hero) {
        hero.setId(id);
        return heroRepository.save(hero);
    }

    public void deleteHero(Long id) {
        heroRepository.deleteById(id);
    }

    public List<Hero> findByName(String name) {
        return heroRepository.findByNameContaining(name);
    }
}

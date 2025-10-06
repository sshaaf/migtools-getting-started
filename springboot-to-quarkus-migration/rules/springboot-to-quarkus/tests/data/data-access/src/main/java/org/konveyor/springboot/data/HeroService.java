package org.konveyor.springboot.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class HeroService {

    @Autowired
    private HeroRepository heroRepository;

    @Transactional(readOnly = true)
    public List<Hero> findAllHeroes() {
        return heroRepository.findAll();
    }

    @Transactional(rollbackFor = Exception.class)
    public Hero persistHero(Hero hero) {
        return heroRepository.save(hero);
    }

    public void deleteHero(Long id) {
        heroRepository.deleteById(id);
    }

    public long countHeroes() {
        return heroRepository.count();
    }
}

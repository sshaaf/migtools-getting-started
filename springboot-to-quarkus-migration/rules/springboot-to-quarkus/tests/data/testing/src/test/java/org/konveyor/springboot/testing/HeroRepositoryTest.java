package org.konveyor.springboot.testing;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class HeroRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private HeroRepository heroRepository;

    @Test
    @Rollback
    void shouldFindHeroesByName() {
        Hero hero = new Hero();
        hero.setName("Superman");
        hero.setLevel(10);
        entityManager.persistAndFlush(hero);

        var heroes = heroRepository.findByNameContainingIgnoreCase("super");

        assertThat(heroes).hasSize(1);
        assertThat(heroes.get(0).getName()).isEqualTo("Superman");
    }
}

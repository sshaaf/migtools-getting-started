package org.konveyor.springboot.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface HeroRepository extends JpaRepository<Hero, Long> {

    List<Hero> findByNameContainingIgnoreCase(String name);
    
    Optional<Hero> findByEmail(String email);

    @Query("SELECT h FROM Hero h WHERE h.level >= :level")
    List<Hero> findHeroesWithLevelGreaterThan(@Param("level") int level);

    @Modifying
    @Transactional
    @Query("DELETE FROM Hero h WHERE h.level < :level")
    int deleteWeakHeroes(@Param("level") int level);

    Page<Hero> findByLevel(int level, Pageable pageable);

    List<Hero> findByPowerOrderByLevelDesc(String power, Sort sort);
}

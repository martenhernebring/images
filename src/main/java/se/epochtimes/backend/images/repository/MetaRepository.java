package se.epochtimes.backend.images.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.epochtimes.backend.images.model.Meta;

@Repository("metaRepository")
public interface MetaRepository extends JpaRepository<Meta, Long> {
}

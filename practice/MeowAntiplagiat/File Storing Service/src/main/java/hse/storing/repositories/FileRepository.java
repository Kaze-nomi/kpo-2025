package hse.storing.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import hse.storing.domains.File;

public interface FileRepository extends JpaRepository<File, Integer> {

    Optional<File> findByHash(int hashCode);

}
package hse.analysis.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import hse.analysis.domains.Analysis;

public interface FileRepository extends JpaRepository<Analysis, Integer> {

}
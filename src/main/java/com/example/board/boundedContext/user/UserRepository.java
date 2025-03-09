package com.example.board.boundedContext.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface UserRepository extends JpaRepository<SiteUser, Long> {

	Optional<SiteUser> findByUsername(String username);

	@Modifying
	@Transactional
	@Query(value = "ALTER TABLE site_user AUTO_INCREMENT = 1", nativeQuery = true)
	void clearAutoIncrement();
}
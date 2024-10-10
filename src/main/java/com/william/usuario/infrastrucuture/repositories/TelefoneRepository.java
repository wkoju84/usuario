package com.william.usuario.infrastrucuture.repositories;


import com.william.usuario.infrastrucuture.entities.Telefone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TelefoneRepository extends JpaRepository<Telefone, Long> {
}

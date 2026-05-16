package org.example.backend.repository;

import org.example.backend.model.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClientRepository extends JpaRepository<ClientEntity, Integer> {

    @Query(value = """
                SELECT DISTINCT ON (c.clientid)
                    c.clientid,
                    c.client_name,
                    c.added,
                    c.type,
                    a.addressid,
                    a.ip,
                    a.mac,
                    a.model,
                    a.address AS address_text            
                FROM  client c LEFT JOIN address a on a.clientid =  c.clientid
                WHERE (c.type = :filterType OR :filterType is null)
                AND ((LOWER(c.client_name) ILIKE CONCAT('%', :searchText, '%') OR :searchText IS NULL) 
                    OR (LOWER(a.address) ILIKE CONCAT('%', :searchText, '%') OR :searchText IS NULL))
           
            """, nativeQuery = true)
    List<ClientEntity> filterByTypeAndText(@Param("filterType") String filterType, @Param("searchText") String searchText);
}

//SELECT * FROM  client c
//left join address a on a.clientid = c.clientid
//WHERE c.type = 'Individual' AND (LOWER (c.client_name) LIKE '%алекс%' OR LOWER (a.address) LIKE '%дом%')
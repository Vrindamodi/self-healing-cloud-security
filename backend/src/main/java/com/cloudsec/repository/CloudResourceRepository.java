package com.cloudsec.repository;

import com.cloudsec.entity.CloudResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CloudResourceRepository extends JpaRepository<CloudResource, Integer> {
    List<CloudResource> findByResourceType(String resourceType);
}

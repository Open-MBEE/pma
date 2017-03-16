package gov.nasa.jpl.repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import gov.nasa.jpl.model.Job;

@RepositoryRestResource
public interface JobRepository extends CrudRepository<Job, Long> {

}

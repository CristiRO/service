package ro.unibuc.hello.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ro.unibuc.hello.model.TodoEntity;

@Repository
public interface TodoRepository extends MongoRepository<TodoEntity, String> {

    List<TodoEntity> findByAssignedUserId(String assignedUserId);
}

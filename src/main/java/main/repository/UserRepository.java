package main.repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import main.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  User findById(int Id);

  Optional<User> findByEmail(String email);

  List<User> findByIsModerator(byte isModerator);// 0 - user, 1 - moderator

  Optional<User> findByCode(String code);

  @Query(nativeQuery = true, value = "SELECT e_mail FROM blog_user")
  HashSet<String> findAllEmails();

  @Query(nativeQuery = true, value = "SELECT name FROM blog_user WHERE id = :userId")
  String findNameById(int userId);
}
package com.hanzec.yats.repository.data;

import com.hanzec.yats.model.data.management.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

}

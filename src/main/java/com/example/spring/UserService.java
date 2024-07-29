package com.example.spring;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class UserService implements UserRepository{

    private final JdbcClient jdbcClient;

    public UserService(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<User> getAllUsers() {
        String sql = "select * from users";
        return jdbcClient.sql(sql)
                .query(User.class)
                .list();
    }

    @Override
    public User getById(Long id) {
        String sql = "select * from users where id = ?";
        return jdbcClient.sql(sql)
                .param(id)
                .query(User.class)
                .single();
    }

    @Override
    public void createUser(User user) {
        String sql = "insert into users (name,email) values (?,?)";
        jdbcClient.sql(sql)
                .params(user.getName(),user.getEmail())
                .update();
    }

    @Override
    public void updateUser(Long id, User user) {
        String sql = "update users set name = ?, email = ? where id = ?";
        jdbcClient.sql(sql)
                .params(user.getName(),user.getEmail(),id)
                .update();
    }

    @Override
    public void deleteUser(Long id) {
          String sql = "delete from users where id = ?";
          var delete = jdbcClient.sql(sql)
                  .param(id)
                  .update();

        Assert.state(delete == 1 , "Failed to delete user" + id);

    }
}

package repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.User;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    private final File file = new File("users.json");
    private final ObjectMapper mapper = new ObjectMapper();

    // 전체 조회
    public List<User> findAll() {
        try {
            if (!file.exists() || file.length() == 0) {
                return new ArrayList<>();
            }
            return mapper.readValue(file, new TypeReference<List<User>>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //ID로 유저 확인
    public User findById(String userId) {
        return findAll().stream()
                .filter(u -> u.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    // 저장
    public void save(User user) {
        List<User> users = findAll();
        users.add(user);

        try {
            mapper.writeValue(file, users);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // ID 중복 체크
    public boolean existsById(String userId) {
        return findAll().stream()
                .anyMatch(u -> u.getUserId().equals(userId));
    }
}
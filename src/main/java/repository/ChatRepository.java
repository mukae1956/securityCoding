package repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.Chat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatRepository {

    private final File file = new File("message.json");
    private final ObjectMapper mapper = new ObjectMapper();

    // 전체 조회
    public List<Chat> findAll() {
        try {
            if (!file.exists() || file.length() == 0) {
                return new ArrayList<>();
            }
            return mapper.readValue(file, new TypeReference<List<Chat>>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 저장
    public void save(Chat chat) {
        List<Chat> chats = findAll();
        chats.add(chat);

        try {
            mapper.writeValue(file, chats);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

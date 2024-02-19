package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class ItemRequestServiceIntegrationTest {
    private final EntityManager manager;
    private final ItemRequestService service;

    private final User requester = new User(null, "имя", "имя.name@mail.rk");
    private final User owner = new User(null, "имяя", "name.Name@mail.rk");
    private final ItemRequest request = new ItemRequest(null, "667", requester, LocalDateTime.now());
    private final Item item = new Item(null, "911", "699HP", true, owner, request);

    @BeforeEach
    void setUp() {
        manager.persist(requester);
        manager.persist(owner);
        manager.persist(request);
        manager.persist(item);
    }

    @Test
    void getByUserId() {
        List<ItemRequestDto> requests = service.getByUserId(requester.getId());

        assertNotNull(requests);
        assertEquals(1, requests.size());
        assertNotNull(requests.get(0).getItems());
        assertEquals(requests.get(0).getId(), request.getId());
        assertEquals(requests.get(0).getDescription(), request.getDescription());
    }
}

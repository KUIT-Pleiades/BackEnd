package com.pleiades.service;

import com.pleiades.strings.ValidationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DuplicationServiceTest {

    @Mock
    private JpaRepository<TestEntity, String> repository;

    @Test
    @DisplayName("responseIdDuplication - ID가 이미 존재할 때 CONFLICT 반환")
    void responseIdDuplication_idExists_returnsConflict() {
        // given
        String id = "existing_id";
        TestEntity entity = new TestEntity();
        entity.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(entity));

        DuplicationService<TestEntity> service = new DuplicationService<>(repository);

        // when
        ResponseEntity<Map<String, Object>> response = service.responseIdDuplication(id);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).containsEntry("available", false);
        assertThat(response.getBody()).containsEntry("message", "The username is already taken.");
    }

    @Test
    @DisplayName("responseIdDuplication - ID가 존재하지 않을 때 OK 반환")
    void responseIdDuplication_idNotExists_returnsOk() {
        // given
        String id = "new_id";

        when(repository.findById(id)).thenReturn(Optional.empty());

        DuplicationService<TestEntity> service = new DuplicationService<>(repository);

        // when
        ResponseEntity<Map<String, Object>> response = service.responseIdDuplication(id);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("available", true);
        assertThat(response.getBody()).containsEntry("message", "The username is available.");
    }

    @Test
    @DisplayName("responseIdDuplication - null ID 처리")
    void responseIdDuplication_nullId_returnsOk() {
        // given
        String id = null;

        when(repository.findById(id)).thenReturn(Optional.empty());

        DuplicationService<TestEntity> service = new DuplicationService<>(repository);

        // when
        ResponseEntity<Map<String, Object>> response = service.responseIdDuplication(id);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("available", true);
    }

    // 테스트용 엔티티 클래스
    static class TestEntity {
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}


package org.springframework;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

/**
 * Created by jose- on 8/24/2021
 **/

@ExtendWith(MockitoExtension.class)
public class JUnitExtensionTest {

    @Mock
    Map<String, Object> mapMock;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testMock() {
        mapMock.put("keyValue", "Hey");
    }
}

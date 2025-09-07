package es;

import com.api.mapper.post.es.PostDetailEsMapper;
import com.czy.api.domain.Do.post.post.PostDetailEsDo;
import com.czy.test.TestApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * @author 13225
 * @date 2025/8/15 17:28
 */

@Slf4j
@SpringBootTest(classes = TestApplication.class)
@TestPropertySource("classpath:application.yml")
public class ElasticSearchTests {

    @Autowired
    private PostDetailEsMapper postDetailEsMapper;

    @Test
    public void esTest(){
        log.info("postDetailEsMapper: {}", postDetailEsMapper);
        Iterable<PostDetailEsDo> results = postDetailEsMapper.findAll();
        results.forEach(post -> log.info("PostDetail: {}", post));
    }

}

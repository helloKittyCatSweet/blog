package com.kitty.blog.domain.repository.search;

import com.kitty.blog.domain.model.search.PostIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PostIndexRepository extends ElasticsearchRepository<PostIndex, Integer> {
}
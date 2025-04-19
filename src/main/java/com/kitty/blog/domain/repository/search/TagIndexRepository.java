package com.kitty.blog.domain.repository.search;

import com.kitty.blog.domain.model.search.TagIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface TagIndexRepository extends ElasticsearchRepository<TagIndex, Integer> {
}

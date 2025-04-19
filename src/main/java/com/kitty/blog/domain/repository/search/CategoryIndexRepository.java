package com.kitty.blog.domain.repository.search;

import com.kitty.blog.domain.model.search.CategoryIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CategoryIndexRepository extends ElasticsearchRepository<CategoryIndex, Integer> {
}

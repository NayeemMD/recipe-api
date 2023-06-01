package com.food.integrationtests.v1.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SearchQuery {
    private QueryFieldName fieldName;

    private Object value;

    private QueryOperator operator;
}

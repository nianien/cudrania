package com.cudrania.core.collection.map;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 可以将两种数据类型绑定,用作{@link DuoMap}对象Value值的数据类型
 *
 * @param <V1>
 * @param <V2>
 * @author skyfalling
 */
@Data
@AllArgsConstructor
public class DuoValue<V1, V2> {

    private V1 value1;
    private V2 value2;


}

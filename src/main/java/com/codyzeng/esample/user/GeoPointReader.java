package com.codyzeng.esample.user;


import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.reader.ObjectReader;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

/**
 * @author: codyzeng@163.com
 * @date: 2022/12/8
 */
public class GeoPointReader implements ObjectReader<GeoPoint> {
    public static final GeoPointReader INSTANCE = new GeoPointReader();

    @Override
    public GeoPoint readObject(JSONReader jsonReader, Type type, Object o, long l) {
        Map<String, Object> geo = jsonReader.readObject();
        BigDecimal lat = Objects.requireNonNullElse((BigDecimal) geo.get("lat"),BigDecimal.ZERO);
        BigDecimal lon = Objects.requireNonNullElse((BigDecimal) geo.get("lon"),BigDecimal.ZERO);
        return new GeoPoint(lat.doubleValue(), lon.doubleValue());
    }
}

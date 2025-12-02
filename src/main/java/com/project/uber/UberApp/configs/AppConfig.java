package com.project.uber.UberApp.configs;

import com.project.uber.UberApp.dto.PointDto;
import com.project.uber.UberApp.utils.GeometryUtil;
import org.locationtech.jts.geom.Point;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    ModelMapper getModelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.typeMap(PointDto.class, Point.class).setConverter(converter -> {
            PointDto pointDto = converter.getSource();
            return GeometryUtil.createPoint(pointDto);
        });

        modelMapper.typeMap(Point.class, PointDto.class).setConverter(converter -> {
            Point point = converter.getSource();
            double coordinates[] = {
                    point.getX(),
                    point.getY()
            };
            return new PointDto(coordinates);
        });

        return modelMapper;
    }
}

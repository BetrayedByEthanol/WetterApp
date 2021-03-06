package org.WetterApp.Mapping;

import org.WetterApp.Data.Interfaces.IDbModelContext;
import org.WetterApp.Data.Interfaces.IWetterSensorContext;
import org.WetterApp.Models.WetterDatenModel;
import org.WetterApp.Models.DataTransitionalObjects.WetterDatenDTO;
import org.WetterApp.Models.WetterSensorModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.OffsetDateTime;

@Mapper
public interface WetterDatenMapper {
    WetterDatenMapper MAPPER = Mappers.getMapper(WetterDatenMapper.class);

    @Mapping(source = "zeitDesMessens", target = "zeitDesMessens",qualifiedByName = "OffSetDT")
    @Mapping(source = "zeitDesMessens", target = "zeitDerLetztenAederung",qualifiedByName = "OffSetDT")
    @Mapping(source = "gemessenVon", target = "gemessenVon", qualifiedByName = "Sensor")
    @Mapping(target = "id", ignore = true)
    WetterDatenModel map(WetterDatenDTO wetterDatenDTO);

    @Named("OffSetDT")
    default OffsetDateTime map(String value){ return OffsetDateTime.parse(value); }

    @Named("Sensor")
    default WetterSensorModel map(int sensorId){
        try(IWetterSensorContext sensorContext = IDbModelContext.MODEL_CONTEXT.getWetterSensorContext()){
            return sensorContext.ladeWetterSensor(sensorId);
        }catch (Exception ex){
            System.out.println(ex.getMessage());
            return null;
        }
    }
}

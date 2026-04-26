package com.freelance.agency.mapper;

import com.freelance.agency.dto.response.BookingResponse;
import com.freelance.agency.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    @Mapping(source = "lead.id",    target = "leadId")
    @Mapping(source = "lead.name",  target = "leadName")
    @Mapping(source = "lead.email", target = "leadEmail")
    BookingResponse toResponse(Booking booking);

    List<BookingResponse> toResponseList(List<Booking> bookings);
}
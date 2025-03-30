package br.com.meetime.hubspot.model.mapper;

import br.com.meetime.hubspot.model.dto.ContactDTO;
import br.com.meetime.hubspot.model.entity.Contact;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ContactMapper {
    ContactMapper INSTANCE = Mappers.getMapper(ContactMapper.class);

    Contact dtoToEntity(ContactDTO pessoaDTO);

    ContactDTO entityToDto(Contact pessoaEntity);
}

package br.api.fynno.mapper;

import br.api.fynno.model.entity.User;
import br.api.fynno.response.user.UserDetailResponse;
import br.api.fynno.response.user.UserSummaryResponse;
import org.springframework.stereotype.Component;

// Adicione esta anotação para que o mapper esteja disponível
// em todos os locais da aplicação.
@Component
public class UserMapper {

    public UserDetailResponse toUserDetailResponse(User user) {
        return new UserDetailResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getActive(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    public UserSummaryResponse toUserSummaryResponse(User user) {
        return new UserSummaryResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getActive()
        );
    }

}

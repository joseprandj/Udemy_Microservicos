package io.github.joseprandj.msCartoes.application.dto;

import io.github.joseprandj.msCartoes.application.domain.ClienteCartao;
import io.github.joseprandj.msCartoes.application.enums.BandeiraCartao;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteCartaoDTO {
    private String nome;
    private String bandeira;
    private BigDecimal limiteLiberado;

    public static ClienteCartaoDTO toDTO(ClienteCartao entity) {
        return new ClienteCartaoDTO(
            entity.getCartao().getNome(),
            entity.getCartao().getBandeiraCartao().toString(),
            entity.getLimite()
        );
    }
}

package io.github.joseprandj.msCartoes.application.dto;

import io.github.joseprandj.msCartoes.application.domain.Cartao;
import io.github.joseprandj.msCartoes.application.enums.BandeiraCartao;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartaoDTO {
    private String nome;
    private BandeiraCartao bandeira;
    private BigDecimal renda;
    private BigDecimal limite;

    public Cartao toEntity(CartaoDTO request) {
        return new Cartao(nome, bandeira, renda, limite);
    }
}

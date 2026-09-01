package io.github.joseprandj.msCartoes.application.service;

import io.github.joseprandj.msCartoes.application.domain.Cartao;
import io.github.joseprandj.msCartoes.application.infra.repository.CartaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartaoService {

    private final CartaoRepository cartaoRepository;

    @Transactional
    public Cartao save(Cartao cartao) {
        return cartaoRepository.save(cartao);
    }

    public List<Cartao> getCartoesRendaMenorIgual(Long renda) {
        return cartaoRepository.findByRendaLessThanEqual(new BigDecimal(renda));
    };
}

package io.github.joseprandj.msCartoes.application.service;

import io.github.joseprandj.msCartoes.application.domain.ClienteCartao;
import io.github.joseprandj.msCartoes.application.infra.repository.ClienteCartaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteCartaoService {
    private final ClienteCartaoRepository repository;

    public List<ClienteCartao> findByCpf(String cpf) {
        return repository.findByCpf(cpf);
    }
}

package io.github.joseprandj.msCartoes.application.controller;

import io.github.joseprandj.msCartoes.application.domain.Cartao;
import io.github.joseprandj.msCartoes.application.domain.ClienteCartao;
import io.github.joseprandj.msCartoes.application.dto.CartaoDTO;
import io.github.joseprandj.msCartoes.application.dto.ClienteCartaoDTO;
import io.github.joseprandj.msCartoes.application.service.CartaoService;
import io.github.joseprandj.msCartoes.application.service.ClienteCartaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("cartoes")
@RequiredArgsConstructor
public class CartoesController {

    private final CartaoService cartaoService;
    private final ClienteCartaoService clienteCartaoService;

    @GetMapping
    public String status() {
        return "OK";
    }

    @GetMapping(params = "renda")
    public ResponseEntity<List<Cartao>> getCartoesRendaAte(@RequestParam Long renda) {
        List<Cartao> cartoes = cartaoService.getCartoesRendaMenorIgual(renda);

        return ResponseEntity.ok(cartoes);
    }

    @GetMapping(params = "cpf")
    public ResponseEntity<List<ClienteCartaoDTO>> getCartoesByCpf(@RequestParam String cpf) {
        List<ClienteCartao> cartoes = clienteCartaoService.findByCpf(cpf);

        return ResponseEntity.ok(
            cartoes.stream()
                .map(ClienteCartaoDTO::toDTO)
                .collect(Collectors.toList())
        );
    }

    @PostMapping
    public ResponseEntity<Cartao> cadastrarCartao(@RequestBody CartaoDTO request) {
        Cartao cartao = cartaoService.save(request.toEntity(request));

        return ResponseEntity.status(HttpStatus.CREATED).body(cartao);
    }

}

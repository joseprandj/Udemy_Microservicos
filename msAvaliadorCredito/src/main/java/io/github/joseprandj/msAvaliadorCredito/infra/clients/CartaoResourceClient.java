package io.github.joseprandj.msAvaliadorCredito.infra.clients;

import io.github.joseprandj.msAvaliadorCredito.domain.Cartao;
import io.github.joseprandj.msAvaliadorCredito.domain.CartaoCliente;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "msCartoes", path = "/cartoes")
public interface CartaoResourceClient {

    @GetMapping(params = "cpf")
    ResponseEntity<List<CartaoCliente>> getCartoesByCpf(@RequestParam String cpf);

    @GetMapping(params = "renda")
    ResponseEntity<List<Cartao>> getCartoesRendaAte(@RequestParam Long renda);
}

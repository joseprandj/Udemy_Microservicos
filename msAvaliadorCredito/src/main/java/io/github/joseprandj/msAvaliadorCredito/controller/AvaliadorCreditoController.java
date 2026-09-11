package io.github.joseprandj.msAvaliadorCredito.controller;

import io.github.joseprandj.msAvaliadorCredito.domain.*;
import io.github.joseprandj.msAvaliadorCredito.exception.DadosClienteNotFoundException;
import io.github.joseprandj.msAvaliadorCredito.exception.ErroComunicacaoMicroserviceException;
import io.github.joseprandj.msAvaliadorCredito.exception.ErroSolicitacaoCartaoException;
import io.github.joseprandj.msAvaliadorCredito.service.AvaliadorCreditoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("avaliacoes-credito")
@RequiredArgsConstructor
public class AvaliadorCreditoController {

    private final AvaliadorCreditoService avaliadorCreditoService;

    @GetMapping
    public String status(){
        return "OK";
    }

    @GetMapping(value = "situacao-cliente", params = "cpf")
    public ResponseEntity consultaSituacaoCliente(@RequestParam("cpf") String cpf){
        try {
            SituacaoCliente situacaoCliente = avaliadorCreditoService.obterSituacaoCliente(cpf);
            return ResponseEntity.ok(situacaoCliente);
        } catch (DadosClienteNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (ErroComunicacaoMicroserviceException e) {
            return ResponseEntity.status(HttpStatus.resolve(e.getStatus())).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity realizarAvalicao(@RequestBody DadosAvaliacao dadosAvalicao){
        try {
            RetornoAvaliacaoCliente retornoAvaliacaoCliente = avaliadorCreditoService.realizarAvaliacao(dadosAvalicao.getCpf(), dadosAvalicao.getRenda());
            return ResponseEntity.ok(retornoAvaliacaoCliente);
        } catch (DadosClienteNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (ErroComunicacaoMicroserviceException e) {
            return ResponseEntity.status(HttpStatus.resolve(e.getStatus())).body(e.getMessage());
        }
    }

    @PostMapping("solicitacoes-cartao")
    public ResponseEntity solicitarCartao(@RequestBody DadosSolicitacaoEmissaoCartao dados){
        try {
            ProtocoloEmissaoCartao protocolo = avaliadorCreditoService.solicitarEmissaoCartao(dados);
            return ResponseEntity.ok(protocolo);
        } catch (ErroSolicitacaoCartaoException ex){
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }
    }


}

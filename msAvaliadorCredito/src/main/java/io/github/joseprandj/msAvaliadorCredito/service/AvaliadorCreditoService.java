package io.github.joseprandj.msAvaliadorCredito.service;

import feign.FeignException;
import io.github.joseprandj.msAvaliadorCredito.domain.*;
import io.github.joseprandj.msAvaliadorCredito.exception.DadosClienteNotFoundException;
import io.github.joseprandj.msAvaliadorCredito.exception.ErroComunicacaoMicroserviceException;
import io.github.joseprandj.msAvaliadorCredito.exception.ErroSolicitacaoCartaoException;
import io.github.joseprandj.msAvaliadorCredito.infra.clients.CartaoResourceClient;
import io.github.joseprandj.msAvaliadorCredito.infra.clients.ClienteResourceClient;
import io.github.joseprandj.msAvaliadorCredito.infra.mqueue.SolicitacaoEmissaoCartaoPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvaliadorCreditoService {

    private final ClienteResourceClient clientesClient;
    private final CartaoResourceClient cartaoClient;
    private final SolicitacaoEmissaoCartaoPublisher emissaoCartaoPublisher;

    public SituacaoCliente obterSituacaoCliente(String cpf) throws DadosClienteNotFoundException, ErroComunicacaoMicroserviceException {
        try {
            ResponseEntity<DadosCliente> dadosClienteResponse = clientesClient.dadosCliente(cpf);
            ResponseEntity<List<CartaoCliente>> cartaoClienteResponse = cartaoClient.getCartoesByCpf(cpf);

            return SituacaoCliente
                    .builder()
                    .cliente(dadosClienteResponse.getBody())
                    .cartoes(cartaoClienteResponse.getBody())
                    .build();
        } catch (FeignException.FeignClientException e) {
            int status = e.status();

            if (status == 404) throw new DadosClienteNotFoundException();

            throw new ErroComunicacaoMicroserviceException(e.getMessage(), status);
        }

    };

    public RetornoAvaliacaoCliente realizarAvaliacao(String cpf, Long renda) throws DadosClienteNotFoundException, ErroComunicacaoMicroserviceException {
        try {
            ResponseEntity<DadosCliente> dadosClienteResponse = clientesClient.dadosCliente(cpf);
            ResponseEntity<List<Cartao>> cartoesResponse = cartaoClient.getCartoesRendaAte(renda);

            List<Cartao> cartoes = cartoesResponse.getBody();
            List<CartaoAprovado> listaCartoesAprovados = cartoes.stream().map(cartao -> {
                DadosCliente dadosCliente = dadosClienteResponse.getBody();

                BigDecimal limiteBasico = cartao.getLimiteBasico();
                BigDecimal idadeBD = BigDecimal.valueOf(dadosCliente.getIdade());
                BigDecimal fator = idadeBD.divide(BigDecimal.TEN);
                BigDecimal limiteAprovado = fator.multiply(limiteBasico);

                CartaoAprovado aprovado = new CartaoAprovado();
                aprovado.setCartao(cartao.getNome());
                aprovado.setBandeira(cartao.getBandeiraCartao());
                aprovado.setLimiteAprovado(limiteAprovado);

                return aprovado;
            }).collect(Collectors.toList());

            return new RetornoAvaliacaoCliente(listaCartoesAprovados);

        } catch (FeignException.FeignClientException e) {
            int status = e.status();

            if (status == 404) throw new DadosClienteNotFoundException();

            throw new ErroComunicacaoMicroserviceException(e.getMessage(), status);
        }
    };

    public ProtocoloEmissaoCartao solicitarEmissaoCartao(DadosSolicitacaoEmissaoCartao dados) {
        try {
            emissaoCartaoPublisher.solicitarCartao(dados);
            return new ProtocoloEmissaoCartao(UUID.randomUUID().toString());
        } catch (Exception e) {
            throw new ErroSolicitacaoCartaoException(e.getMessage());
        }
    }
}

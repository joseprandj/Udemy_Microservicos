package io.github.joseprandj.msClientes.application.dto;

import io.github.joseprandj.msClientes.domain.Cliente;
import lombok.Data;

@Data
public class ClienteDTO {
    private String cpf;
    private String nome;
    private String idade;

    public Cliente toModel(){
        return new Cliente(this.cpf, this.nome, this.idade);
    }
}

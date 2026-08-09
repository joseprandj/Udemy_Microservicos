package io.github.joseprandj.msClientes.application;

import io.github.joseprandj.msClientes.application.dto.ClienteDTO;
import io.github.joseprandj.msClientes.domain.Cliente;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("clientes")
@RequiredArgsConstructor
public class ClienteResource {

    private final ClienteService service;

    @GetMapping
    public String status(){
        return "OK";
    }

    @PostMapping
    public ResponseEntity save(@RequestBody ClienteDTO clienteDTO){
        Cliente cliente = clienteDTO.toModel();
        service.save(cliente);

        URI headerLocation = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .queryParam("cpf={cpf}")
            .buildAndExpand(cliente.getCpf())
            .toUri();

        return ResponseEntity.created(headerLocation).build();
    }

    @GetMapping(params = "cpf")
    public ResponseEntity dadosCliente(@RequestParam("cpf") String cpf){
        Optional<Cliente> cliente = service.getByCPF(cpf);

        if(cliente.isEmpty()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(cliente);
    }
}

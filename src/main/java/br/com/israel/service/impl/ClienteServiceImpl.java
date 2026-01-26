package br.com.israel.service.impl;

import br.com.israel.model.Cliente;
import br.com.israel.model.Endereco;
import br.com.israel.repository.ClienteRepository;
import br.com.israel.repository.EnderecoRepository;
import br.com.israel.service.ClienteService;
import br.com.israel.service.ViaCepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClienteServiceImpl implements ClienteService {

    // Singleton: Injetar os componentes do Spring com @Autowired.
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private EnderecoRepository enderecoRepository;
    @Autowired
    private ViaCepService viaCepService;


    @Override
    public Iterable<Cliente> buscarTodos(){
        return clienteRepository.findAll();
    }
    @Override
    public Cliente buscarPorId(Long id) {
        // Buscar Cliente por ID.
        Optional<Cliente> cliente = clienteRepository.findById(id);
        return cliente.get();
    }

    @Override
    public void inserir(Cliente cliente) {
        salvarClienteComCep(cliente);
    }

    @Override
    public void atualizar(Long id, Cliente cliente) {
        // Buscar Cliente por ID, caso exista:
        Optional<Cliente> clienteBd = clienteRepository.findById(id);
        if (clienteBd.isPresent()) {
            salvarClienteComCep(cliente);
        }
    }

    @Override
    public void deletar(Long id) {
        // Deletar Cliente por ID.
        clienteRepository.deleteById(id);
    }

    private void salvarClienteComCep(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não pode ser nulo");
        } if (cliente.getEndereco() == null) {
            throw new IllegalArgumentException("Endereço é obrigatório");
        } if (cliente.getEndereco().getCep() == null ||
                cliente.getEndereco().getCep().isBlank()) {
            throw new IllegalArgumentException("CEP é obrigatório");
        }
        String cep = cliente.getEndereco().getCep();

        Endereco endereco = enderecoRepository.findById(cep)
                .orElseGet(() -> {
                    Endereco novoEndereco = viaCepService.consultarCep(cep);
                    return enderecoRepository.save(novoEndereco);
                });
        cliente.setEndereco(endereco);
        clienteRepository.save(cliente);
    }

}

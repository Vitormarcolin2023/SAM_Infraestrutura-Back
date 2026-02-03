package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.model.Endereco;
import com.br.SAM_FullStack.SAM_FullStack.repository.EnderecoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;
    private final RestTemplate restTemplate;

    public EnderecoService(EnderecoRepository enderecoRepository) {
        this.enderecoRepository = enderecoRepository;
        this.restTemplate = new RestTemplate();
    }

    // Listar todos
    public List<Endereco> listAll() {
        return enderecoRepository.findAll();
    }

    // Buscar por ID
    public Endereco findById(Long id) {
        return enderecoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado"));
    }

    // Salvar
    public Endereco save(Endereco endereco) {
        preencherEnderecoPeloCep(endereco);
        return enderecoRepository.save(endereco);
    }

    // Atualizar
    public Endereco update(Long id, Endereco enderecoUpdate) {
        Endereco enderecoExistente = findById(id);

        // Atualiza dados
        enderecoExistente.setRua(enderecoUpdate.getRua());
        enderecoExistente.setNumero(enderecoUpdate.getNumero());
        enderecoExistente.setBairro(enderecoUpdate.getBairro());
        enderecoExistente.setCidade(enderecoUpdate.getCidade());
        enderecoExistente.setEstado(enderecoUpdate.getEstado());
        enderecoExistente.setCep(enderecoUpdate.getCep());

        preencherEnderecoPeloCep(enderecoExistente);

        return enderecoRepository.save(enderecoExistente);
    }

    // Deletar
    public void delete(Long id) {
        Endereco endereco = findById(id);
        enderecoRepository.delete(endereco);
    }

    // Busca dados do endereço usando API ViaCEP
    private void preencherEnderecoPeloCep(Endereco endereco) {
        if (endereco.getCep() != null && !endereco.getCep().isBlank()) {
            String url = "https://viacep.com.br/ws/" + endereco.getCep() + "/json/";
            Endereco enderecoApi = restTemplate.getForObject(url, Endereco.class);

            if (enderecoApi != null) {
                if (endereco.getRua() == null || endereco.getRua().isBlank()) {
                    endereco.setRua(enderecoApi.getRua());
                }
                if (endereco.getBairro() == null || endereco.getBairro().isBlank()) {
                    endereco.setBairro(enderecoApi.getBairro());
                }
                if (endereco.getCidade() == null || endereco.getCidade().isBlank()) {
                    endereco.setCidade(enderecoApi.getCidade());
                }
                if (endereco.getEstado() == null || endereco.getEstado().isBlank()) {
                    endereco.setEstado(enderecoApi.getEstado());
                }
            }
        }
    }
}

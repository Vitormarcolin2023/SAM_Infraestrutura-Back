package com.br.SAM_FullStack.SAM_FullStack.service;


import com.br.SAM_FullStack.SAM_FullStack.model.Aluno;
import com.br.SAM_FullStack.SAM_FullStack.model.AreaDeAtuacao;
import com.br.SAM_FullStack.SAM_FullStack.repository.AreaDeAtuacaoRepository;
import com.br.SAM_FullStack.SAM_FullStack.model.Aluno;
import org.springframework.security.core.Authentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AreaDeAtuacaoService {

    private final AreaDeAtuacaoRepository areaDeAtuacaoRepository;


    public AreaDeAtuacao findById(Long id) {
        return areaDeAtuacaoRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Area de Atuação não encontrada com ID: " + id));
    }

    public List<AreaDeAtuacao> findAll() {
        return areaDeAtuacaoRepository.findAll();
    }

    public AreaDeAtuacao save(AreaDeAtuacao areaDeAtuacao) {
        return areaDeAtuacaoRepository.save(areaDeAtuacao);
    }

    public AreaDeAtuacao update(Long id, AreaDeAtuacao areaDeAtuacaoUpdate){
        AreaDeAtuacao areaDeAtuacaoExistente = findById(id);
        areaDeAtuacaoExistente.setNome(areaDeAtuacaoUpdate.getNome());

        return areaDeAtuacaoRepository.save(areaDeAtuacaoExistente);
    }

    public void delete(Long id){
        AreaDeAtuacao areaDeAtuacao = findById(id);
        areaDeAtuacaoRepository.delete(areaDeAtuacao);
    }

    public List<AreaDeAtuacao> saveAll(List<AreaDeAtuacao> areaDeAtuacoes) {
        for (AreaDeAtuacao areaDeAtuacao : areaDeAtuacoes) {
            this.save(areaDeAtuacao);
        }
        return areaDeAtuacoes;
    }

    public List<AreaDeAtuacao> buscarPorInicioDoNome(String prefixo) {
        return areaDeAtuacaoRepository.findByNomeStartingWithIgnoreCase(prefixo);
    }

    public AreaDeAtuacao findByAlunoLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Aluno) {
            Aluno alunoLogado = (Aluno) principal;
            if (alunoLogado.getCurso() != null) {
                return alunoLogado.getCurso().getAreaDeAtuacao();
            }
        }
        return null;
    }
}

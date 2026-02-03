package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.model.*;
import com.br.SAM_FullStack.SAM_FullStack.repository.AvaliacaoRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.GrupoRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.MentorRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.ProjetoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProjetoService {
    private final ProjetoRepository projetoRepository;
    private final GrupoRepository grupoRepository;

    @Autowired
    private MentorRepository mentorRepository;

    public ProjetoService(ProjetoRepository projetoRepository, GrupoRepository grupoRepository) {
        this.projetoRepository = projetoRepository;
        this.grupoRepository = grupoRepository;
    }

    //LISTAR PROJETO
    public List<Projeto> listAll() {
        return projetoRepository.findAll();
    }

    //BUSCAR POR ID
    public Projeto findById(long id) {
        return projetoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));
    }

    public List<Projeto> buscarPorNome(String nomeDoProjeto) {
        return projetoRepository.findByNomeDoProjetoIgnoreCaseContaining(nomeDoProjeto);
    }

    public List<Projeto> buscarPorAreaAtuacao(AreaDeAtuacao areaDeAtuacao) {
        return projetoRepository.findByAreaDeAtuacao(areaDeAtuacao);

    }
    public List<Projeto> findByPeriodo(String periodo) {
        return projetoRepository.findByPeriodo(periodo);
    }

    private void atualizarStatusProjeto(Projeto projeto) {
        LocalDate hoje = LocalDate.now();

        if (projeto.getDataFinalProjeto() != null && projeto.getDataInicioProjeto().isAfter(hoje)) {
         projeto.setStatusProjeto(StatusProjeto.EM_APROVACAO);
        } else if (projeto.getDataFinalProjeto() != null && projeto.getDataFinalProjeto().isAfter(hoje)) {
            projeto.setStatusProjeto(StatusProjeto.ATIVO);
        } else if (projeto.getDataFinalProjeto() != null && projeto.getDataFinalProjeto().isBefore(hoje)) {
            projeto.setStatusProjeto(StatusProjeto.ATIVO);
        } else {
            projeto.setStatusProjeto(StatusProjeto.ATIVO);
        }

    }

    // SALVAR
    public Projeto save(Projeto projeto) {
        if (projeto.getGrupo() != null && projeto.getGrupo().getId() != null) {
            Grupo grupoGerenciado = grupoRepository.findByIdWithAlunos(projeto.getGrupo().getId())
                    .orElse(projeto.getGrupo());
            projeto.setGrupo(grupoGerenciado);
            if (grupoGerenciado.getAlunos() != null) {
                for (Aluno aluno : grupoGerenciado.getAlunos()) {
                    if (aluno.getCurso() != null
                            && aluno.getCurso().getAreaDeAtuacao() != null
                            && projeto.getAreaDeAtuacao() != null
                            && !aluno.getCurso().getAreaDeAtuacao().getId()
                            .equals(projeto.getAreaDeAtuacao().getId())) {
                        throw new IllegalArgumentException(
                                "A área de atuação do projeto deve ser a mesma de todos os alunos do grupo."
                        );
                    }
                }

            }
        }
        atualizarStatusProjeto(projeto);
        projeto.setAvaliadoPorMentor(false);
        return projetoRepository.save(projeto);
    }


    //atualizar
    public Projeto update (Long id, Projeto projetoUpdate){
        Projeto projetoExistente = findById(id);
        projetoExistente.setNomeDoProjeto(projetoUpdate.getNomeDoProjeto());
        projetoExistente.setDataInicioProjeto(projetoUpdate.getDataInicioProjeto());
        projetoExistente.setDataFinalProjeto(projetoUpdate.getDataFinalProjeto());
        projetoExistente.setDescricao(projetoUpdate.getDescricao());
        projetoExistente.setPeriodo(projetoUpdate.getPeriodo());
        projetoExistente.setAreaDeAtuacao(projetoUpdate.getAreaDeAtuacao());

        atualizarStatusProjeto(projetoExistente);

        return projetoRepository.save(projetoExistente);
    }

    public void delete(Long id){
        Projeto projeto = findById(id);
        projetoRepository.delete(projeto);
    }

    @Transactional
    public void desvincularMentor(Long mentorId) {
        List<Projeto> projetos = projetoRepository.findByMentorId(mentorId);
        for (Projeto p : projetos) {
            p.setMentor(null);
            projetoRepository.save(p);
        }
    }

    public List<Projeto> findByMentor(Long mentorId) {
        List<Projeto> projetos = projetoRepository.findByMentorId(mentorId);

        if (projetos.isEmpty()) {
            throw new RuntimeException("Nenhum projeto encontrado para este mentor.");
        }

        return projetos;
    }

    public List<Projeto> buscarProjetosPorProfessor(Long professorId) {
        return projetoRepository.findAllByProfessoresId(professorId);
    }

    public Projeto buscarProjetoAtivo(Long alunoId){
        return projetoRepository.findProjetoPorStatusAluno(alunoId, StatusProjeto.ATIVO)
                .orElseThrow(() -> new RuntimeException("Nenhum projeto ativo no momento"));
    }

    public List<Projeto> buscarProjetosAtivosMentores(Long mentorId){
        return projetoRepository.findAllByMentorIdAndStatusProjeto(mentorId, StatusProjeto.ATIVO);
    }

    public List<Projeto> buscarProjetosAguardandoAvaliacaoMentor(Long mentorId) {
        return projetoRepository.findAllByMentorIdAndStatusProjeto(mentorId, StatusProjeto.AGUARDANDO_AVALIACAO);
    }

    public Projeto buscarProjetoAguardandoAvaliacaoAluno(Long alunoId) {
        return projetoRepository.findProjetoPorStatusAluno(alunoId, StatusProjeto.AGUARDANDO_AVALIACAO)
                .orElseThrow(() -> new RuntimeException("Nenhum projeto aguardando avaliação"));
    }

    public Projeto findByGrupo(Long idGrupo) {
        // O .orElse(null) é importante aqui para não quebrar com erro 500
        // se o projeto não existir. Ele vai retornar null suavemente.
        return projetoRepository.findByGrupoId(idGrupo).orElse(null);
    }

}
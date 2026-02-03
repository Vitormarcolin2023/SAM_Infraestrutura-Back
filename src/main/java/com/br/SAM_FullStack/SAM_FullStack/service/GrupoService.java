package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.dto.GrupoDTO;
import com.br.SAM_FullStack.SAM_FullStack.dto.GrupoUpdateDTO;
import com.br.SAM_FullStack.SAM_FullStack.model.*;
import com.br.SAM_FullStack.SAM_FullStack.repository.AlunoRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.GrupoRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.ProfessorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GrupoService {

    private final GrupoRepository grupoRepository;
    private final AlunoRepository alunoRepository;
    private final ProfessorRepository professorRepository;

    public List<Grupo> findAll() {
        return grupoRepository.findAll();
    }

    public Grupo findById(long id) {
        return grupoRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Grupo não encontrado.")
        );
    }

    public GrupoDTO save(GrupoDTO grupoDTO) {
        Aluno admin = alunoRepository.findById(grupoDTO.alunoAdminId())
                .orElseThrow(() -> new IllegalArgumentException("Aluno administrador não encontrado"));

        // Valida se admin já está em grupo ativo
        admin.getGrupos().forEach(grupo1 -> {
            if (grupo1.getStatusGrupo() == StatusGrupo.ATIVO) {
                throw new IllegalStateException("Operação não permitida. Admin já participa de outro grupo ativo");
            }
        });

        List<Aluno> alunos = alunoRepository.findAllById(grupoDTO.alunosIds());
        if (alunos.size() != grupoDTO.alunosIds().size()) {
            throw new IllegalArgumentException("Um ou mais IDs de alunos não existem");
        }

        List<Professor> professores = professorRepository.findAllById(grupoDTO.professoresIds());
        if (professores.size() != grupoDTO.professoresIds().size()) {
            throw new IllegalArgumentException("Um ou mais professores estão incorretos");
        }

        if (alunos.size() < 3 || alunos.size() > 6) {
            throw new IllegalStateException("Grupo deve ter entre 3 e 6 participantes");
        }

        if (alunos.stream().noneMatch(a -> a.getId().equals(admin.getId()))) {
            throw new IllegalStateException("Administrador informado não participa do grupo");
        }

        for (Aluno aluno : alunos) {
            aluno.getGrupos().forEach(grupo1 -> {
                if (grupo1.getStatusGrupo() == StatusGrupo.ATIVO) {
                    throw new IllegalStateException("Aluno " + aluno.getNome() + " já participa de outro grupo ativo");
                }
            });
        }

        Grupo grupo = new Grupo();
        grupo.setNome(grupoDTO.nome());
        grupo.setAlunoAdmin(admin);
        grupo.setStatusGrupo(StatusGrupo.ATIVO);
        grupo.setProfessores(professores);
        grupo.setPeriodo(grupoDTO.periodo());

        for (Aluno aluno : alunos) {
            aluno.setStatusAlunoGrupo(StatusAlunoGrupo.ATIVO);
            aluno.getGrupos().add(grupo);
        }

        grupo.setAlunos(alunos);

        alunoRepository.saveAll(alunos);
        Grupo salvo = grupoRepository.save(grupo);

        return new GrupoDTO(
                salvo.getId(),
                salvo.getNome(),
                admin.getId(),
                alunos.stream().map(Aluno::getId).toList(),
                professores.stream().map(Professor::getId).toList(),
                salvo.getPeriodo()
        );
    }

    public String updateGrupoInfo(Long groupId, Long adminId, GrupoUpdateDTO grupoUpdateDTO) {
        Grupo grupo = grupoRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Grupo não encontrado."));

        if (grupoUpdateDTO.nome() != null && !grupoUpdateDTO.nome().isBlank()) {
            grupo.setNome(grupoUpdateDTO.nome());
        }

        grupoRepository.save(grupo);
        return "Informações do grupo atualizadas com sucesso.";
    }

    public String adicionarAlunoAoGrupo(Long idAdmin, long idGrupo, Long idAluno) {
        Grupo grupo = grupoRepository.findById(idGrupo)
                .orElseThrow(() -> new IllegalArgumentException("Grupo não encontrado"));


        if (!grupo.getAlunoAdmin().getId().equals(idAdmin)) {
            throw new IllegalStateException("Apenas o admin do grupo pode adicionar alunos");
        }

        if (grupo.getAlunos().size() >= 6) {
            throw new IllegalStateException("O grupo já está no limite de alunos (6)");
        }

        Aluno aluno = alunoRepository.findById(idAluno).orElseThrow(() ->
                new IllegalStateException("Aluno não encontrado"));

        // Valida se aluno já participa de grupo ativo
        aluno.getGrupos().forEach(g -> {
            if (g.getStatusGrupo() == StatusGrupo.ATIVO) {
                throw new IllegalStateException("Aluno " + aluno.getNome() + " já participa de outro grupo ativo");
            }
        });

        aluno.setStatusAlunoGrupo(StatusAlunoGrupo.ATIVO);
        aluno.getGrupos().add(grupo);
        grupo.getAlunos().add(aluno);

        alunoRepository.save(aluno);
        grupoRepository.save(grupo);

        return "Aluno adicionado com sucesso ao grupo";
    }

    public String removerAlunoDiretamente(Long idGrupo, Long idAlunoRemover, Long idAdmin) {
        Grupo grupo = grupoRepository.findById(idGrupo)
                .orElseThrow(() -> new IllegalArgumentException("Grupo não encontrado."));

        /*if (!grupo.getAlunoAdmin().getId().equals(idAdmin)) {
            throw new IllegalStateException("Apenas o administrador pode remover membros.");
        }*/

        if (idAlunoRemover.equals(idAdmin)) {
            throw new IllegalStateException("O administrador não pode remover a si mesmo.");
        }

        Aluno aluno = alunoRepository.findById(idAlunoRemover)
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado."));

        if (!grupo.getAlunos().contains(aluno)) {
            throw new IllegalStateException("O aluno informado não pertence a este grupo.");
        }
        aluno.getGrupos().remove(grupo);
        grupo.getAlunos().remove(aluno);
        aluno.setStatusAlunoGrupo(null);

        alunoRepository.save(aluno);
        grupoRepository.save(grupo);

        return "Aluno " + aluno.getNome() + " foi removido do grupo";
    }

    public List<Grupo> findByAlunosStatusAlunoGrupo(StatusAlunoGrupo statusAlunoGrupo) {
        return grupoRepository.findByAlunosStatusAlunoGrupo(statusAlunoGrupo);
    }

    public String analizarExclusaoAluno(String senhaProf, long idGrupo, long idAluno, boolean resposta) {
        Grupo grupo = grupoRepository.findById(idGrupo)
                .orElseThrow(() -> new IllegalArgumentException("Grupo não encontrado"));

        Professor professor = professorRepository.findBySenha(senhaProf)
                .orElseThrow(() -> new IllegalArgumentException("Professor não encontrado"));

        Aluno aluno = alunoRepository.findById(idAluno)
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado"));

        if (!grupo.getAlunos().contains(aluno) || aluno.getStatusAlunoGrupo() != StatusAlunoGrupo.AGUARDANDO) {
            throw new IllegalStateException("Esse aluno não está aguardando exclusão nesse grupo.");
        }

        if (resposta) {
            aluno.getGrupos().remove(grupo);
            grupo.getAlunos().remove(aluno);
            aluno.setStatusAlunoGrupo(null);
        } else {
            aluno.setStatusAlunoGrupo(StatusAlunoGrupo.ATIVO);
        }

        alunoRepository.save(aluno);
        grupoRepository.save(grupo);

        return resposta ?
                "Aluno " + aluno.getNome() + " foi removido do grupo" :
                "Solicitação de exclusão recusada. O aluno permanece no grupo";
    }

    public String deletarGrupo(long idGrupo, long idProfessor) {
        Grupo grupo = grupoRepository.findById(idGrupo)
                .orElseThrow(() -> new IllegalArgumentException("Grupo não encontrado"));

        Professor professor = professorRepository.findById(idProfessor)
                .orElseThrow(() -> new IllegalArgumentException("Professor não encontrado"));

        for (Aluno aluno : grupo.getAlunos()) {
            aluno.getGrupos().remove(grupo);
            aluno.setStatusAlunoGrupo(null);
            alunoRepository.save(aluno);
        }

        grupoRepository.delete(grupo);

        return "Grupo deletado com sucesso";
    }

    public Grupo findByAluno(Aluno aluno) {

            List<Grupo> grupos = grupoRepository.findByStatusGrupoAndAlunosId(StatusGrupo.ATIVO, aluno.getId());

            if(grupos.isEmpty()){
                 return null;
            }

            // pega o primeiro grupo da lista pois o aluno só poderá ter 1 grupo ativo por projeto
            Grupo grupo = grupos.get(0);

            return grupo;

    }

    public String arquivarGrupo(long idGrupo){
        Grupo grupo = grupoRepository.findById(idGrupo)
                .orElseThrow(() -> new IllegalArgumentException("Grupo não encontrado"));

        grupo.setStatusGrupo(StatusGrupo.ARQUIVADO);
        grupoRepository.save(grupo);

        return "Grupo arquivado com sucesso!";
    }

    public List<Grupo> findByGruposArquivados(long id){
        List<Grupo> grupos = grupoRepository.findByStatusGrupoAndAlunosId(StatusGrupo.ARQUIVADO, id);

        if(grupos.isEmpty()){
            throw new RuntimeException("Aluno não possui nenhum grupo arquivado");
        }

        return grupos;
    }

    public List<Grupo> findGruposByProfessorId(Long professorId) {
        return grupoRepository.findGruposByProfessorId(professorId);
    }
}

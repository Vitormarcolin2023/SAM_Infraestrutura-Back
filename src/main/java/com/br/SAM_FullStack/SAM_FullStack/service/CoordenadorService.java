package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.dto.CoordenadorDTO;
import com.br.SAM_FullStack.SAM_FullStack.dto.CoordenadorUpdateDTO;
import com.br.SAM_FullStack.SAM_FullStack.model.Coordenador;
import com.br.SAM_FullStack.SAM_FullStack.model.Curso;
import com.br.SAM_FullStack.SAM_FullStack.model.Mentor;
import com.br.SAM_FullStack.SAM_FullStack.model.Projeto;
import com.br.SAM_FullStack.SAM_FullStack.repository.CoordenadorRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.CursoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CoordenadorService {

    private final CoordenadorRepository coordenadorRepository;
    private final CursoRepository cursoRepository;
    private final MentorService mentorService;
    private final ProjetoService projetoService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Coordenador save(CoordenadorDTO coordenadorDTO) {
        Coordenador coordenador = new Coordenador();
        coordenador.setNome(coordenadorDTO.getNome());
        coordenador.setEmail(coordenadorDTO.getEmail());
        String senhaEncript = passwordEncoder.encode(coordenadorDTO.getSenha());
        coordenador.setSenha(senhaEncript);

        List<Curso> cursos = cursoRepository.findAllById(coordenadorDTO.getCursosIds());

        for (Curso curso : cursos) {
            curso.setCoordenador(coordenador);
        }

        coordenador.setCursos(cursos);

        return coordenadorRepository.save(coordenador);
    }

    @Transactional
    public String update(CoordenadorUpdateDTO coordenadorDTO, long id) {

        Coordenador coordenadorExistente = coordenadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coordenador não encontrado com o ID: " + id));

        coordenadorExistente.setNome(coordenadorDTO.getNome());
        coordenadorExistente.setEmail(coordenadorDTO.getEmail());
        String senhaEncript = passwordEncoder.encode(coordenadorDTO.getSenha());
        coordenadorExistente.setSenha(senhaEncript);

        if (coordenadorExistente.getCursos() != null) {
            coordenadorExistente.getCursos().forEach(curso -> curso.setCoordenador(null));
        }
        coordenadorExistente.getCursos().clear();

        if (coordenadorDTO.getCursosIds() != null && !coordenadorDTO.getCursosIds().isEmpty()) {

            for (Long cursoId : coordenadorDTO.getCursosIds()) {
                Curso cursoDoBanco = cursoRepository.findById(cursoId)
                        .orElseThrow(() -> new RuntimeException("Curso não encontrado com o ID: " + cursoId));

                cursoDoBanco.setCoordenador(coordenadorExistente);

                coordenadorExistente.getCursos().add(cursoDoBanco);
            }
        }

        coordenadorRepository.save(coordenadorExistente);

        return "Coordenador atualizado com sucesso!";
    }

    @Transactional
    public void delete(Long id) {
        Coordenador coordenadorExistente = coordenadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coordenador não encontrado com o ID: " + id));

        if (coordenadorExistente.getCursos() != null) {
            coordenadorExistente.getCursos().forEach(curso -> curso.setCoordenador(null));
        }

        coordenadorExistente.getCursos().clear();

        cursoRepository.saveAll(coordenadorExistente.getCursos());

        coordenadorRepository.delete(coordenadorExistente);
    }

    public String ativarMentor(long mentorId){
        try {
            String mensagem = this.mentorService.updateStatus(mentorId, "ATIVO");
            return "Mentor ativado com sucesso!";
        } catch (Exception e) {
            return "Erro ao tentar ativar o mentor.";
        }
    }

    public String inativarMentor(long mentorId){
        try {
            String mensagem = this.mentorService.updateStatus(mentorId, "INATIVO");
            return "Mentor inativado com sucesso.";
        } catch (Exception e) {
            return "Erro ao tentar inativar o mentor.";
        }
    }

    public List<Mentor> findAllMentores(){
        return this.mentorService.listAll();
    }

    public List<Projeto> findAllProjetos(){
        return this.projetoService.listAll();
    }

    public Coordenador buscarPorEmail(String email) {
        Optional<Coordenador> coordenador = coordenadorRepository.findByEmail(email);
        return coordenador.orElse(null);
    }

    public Coordenador findById(Long id) {
        return coordenadorRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Coordenador com ID " + id + " não encontrado."));
    }
}
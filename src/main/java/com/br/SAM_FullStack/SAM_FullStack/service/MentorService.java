package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.model.Mentor;
import com.br.SAM_FullStack.SAM_FullStack.model.Projeto;
import com.br.SAM_FullStack.SAM_FullStack.model.StatusMentor;
import com.br.SAM_FullStack.SAM_FullStack.repository.MentorRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.ProjetoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MentorService {
    private final MentorRepository mentorRepository;


    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public MentorService(MentorRepository mentorRepository){
        this.mentorRepository = mentorRepository;
    }

    //listar
    public List<Mentor> listAll(){
        return mentorRepository.findAll();
    }

    //buscar por id
    public Mentor findById(long id){
        return mentorRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Mentor não encontrado"));
    }

    // Salvar
    public Mentor save(Mentor mentor) {
        mentor.setStatusMentor(StatusMentor.PENDENTE);

        String senhaEncript = passwordEncoder.encode(mentor.getSenha());
        mentor.setSenha(senhaEncript);

        Mentor mentorSalvo = mentorRepository.save(mentor);

        try {
            String destinatario = mentor.getEmail();
            String assunto = "Bem-vindo(a) ao SAM - Cadastro em Análise";
            Map<String, Object> variaveis = Map.of("nomeMentor", mentorSalvo.getNome());
            String template = "emails/boasVindasMentor";

            emailService.enviarEmailComTemplate(destinatario, assunto, template, variaveis);
        } catch (Exception e) {
            System.err.println("Erro ao enviar e-mail de boas-vindas: " + e.getMessage());
            e.printStackTrace();
        }

        return mentorSalvo;
    }

    public Mentor update(Long id, Mentor mentorUpdate) {
        Mentor mentorExistente = findById(id);

        // Atualiza campos apenas se vierem preenchidos
        if (mentorUpdate.getNome() != null) mentorExistente.setNome(mentorUpdate.getNome());
        if (mentorUpdate.getCpf() != null) mentorExistente.setCpf(mentorUpdate.getCpf());
        if (mentorUpdate.getEmail() != null) mentorExistente.setEmail(mentorUpdate.getEmail());
        if (mentorUpdate.getTipoDeVinculo() != null) mentorExistente.setTipoDeVinculo(mentorUpdate.getTipoDeVinculo());
        if (mentorUpdate.getFormacaoDoMentor() != null) mentorExistente.setFormacaoDoMentor(mentorUpdate.getFormacaoDoMentor());
        if (mentorUpdate.getTempoDeExperiencia() != null) mentorExistente.setTempoDeExperiencia(mentorUpdate.getTempoDeExperiencia());
        if (mentorUpdate.getAreaDeAtuacao() != null) mentorExistente.setAreaDeAtuacao(mentorUpdate.getAreaDeAtuacao());
        if (mentorUpdate.getEndereco() != null) mentorExistente.setEndereco(mentorUpdate.getEndereco());
        if (mentorUpdate.getResumo() != null) mentorExistente.setResumo(mentorUpdate.getResumo());

        return mentorRepository.save(mentorExistente);
    }


    //deletar
    public void delete(Long id){
        Mentor mentor = findById(id);
        mentorRepository.delete(mentor);
    }

    public String updateStatus(long id, String statusString){
        Mentor mentor = mentorRepository.findById(id).orElseThrow(() -> new RuntimeException("Mentor não encontrado."));

        StatusMentor novoStatus = StatusMentor.valueOf(statusString.toUpperCase());

        mentor.setStatusMentor(novoStatus);
        mentorRepository.save(mentor);

        return "Status do mentor atualizado com sucesso!";
    }

    public Mentor findByEmail(String email) {
        // Usa o repositório para buscar o mentor por e-mail e retorna o objeto ou null
        Optional<Mentor> mentor = mentorRepository.findByEmail(email);
        return mentor.orElse(null);
    }

    public List<Mentor> findByArea(Long idArea) {
        return mentorRepository.findByAreaDeAtuacaoIdAndStatusMentor(idArea, StatusMentor.ATIVO);
    }
}

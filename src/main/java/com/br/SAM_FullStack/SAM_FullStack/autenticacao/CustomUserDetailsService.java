package com.br.SAM_FullStack.SAM_FullStack.autenticacao;

import com.br.SAM_FullStack.SAM_FullStack.repository.AlunoRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.CoordenadorRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.MentorRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AlunoRepository alunoRepo;
    @Autowired
    private MentorRepository mentorRepo;
    @Autowired
    private ProfessorRepository professorRepo;
    @Autowired
    private CoordenadorRepository coordenadorRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
            if (alunoRepo.findByEmail(email).isPresent()) {
                return alunoRepo.findByEmail(email).get();
            } else if (mentorRepo.findByEmail(email).isPresent()) {
                return mentorRepo.findByEmail(email).get();
            } else if (professorRepo.findByEmail(email).isPresent()) {
                return professorRepo.findByEmail(email).get();
            } else if (coordenadorRepo.findByEmail(email).isPresent()) {
                return coordenadorRepo.findByEmail(email).get();
            } else {
                throw new UsernameNotFoundException("Usuário não encontrado");
            }
        }

    }


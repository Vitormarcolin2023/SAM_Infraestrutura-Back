    package com.br.SAM_FullStack.SAM_FullStack.service;

    import com.br.SAM_FullStack.SAM_FullStack.dto.ReuniaoDTO;
    import com.br.SAM_FullStack.SAM_FullStack.model.*;
    import com.br.SAM_FullStack.SAM_FullStack.repository.*;
    import lombok.RequiredArgsConstructor;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;

    import java.util.List;

    @Service
    @RequiredArgsConstructor
    public class ReuniaoService1 {

        @Autowired
        private final ReuniaoRepository reuniaoRepository;
        private final ProjetoRepository projetoRepository;
        private final AlunoRepository alunoRepository;

        // Metodo para coordenação - ver todas
        public List<Reuniao> findAll() {
            return reuniaoRepository.findAll();
        }

        // Metodo para grupo - ver só reuniões do grupo
        public List<Reuniao> findAllByGrupo(long idGrupo) {
            return reuniaoRepository.findAllGrupo(idGrupo);
        }

        // Metodo para mentor - ver só reuniões do mentor
        public List<Reuniao> findAllByMentor(long idMentor) {
            return reuniaoRepository.findAllMentor(idMentor);
        }

        public List<Reuniao> findAllByProjeto(Long projetoId) {
            return reuniaoRepository.findAllByProjetoId(projetoId);
        }

        // Buscar por ID
        public Reuniao findById(long id) {
            return reuniaoRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Reunião não encontrada com id " + id));
        }

        public String save(ReuniaoDTO reuniaoDTO) {
            Projeto projeto = projetoRepository.findById(reuniaoDTO.getProjeto_id()).
                    orElseThrow(() -> new RuntimeException("Projeto não encontrado"));

            Reuniao reuniao = new Reuniao();
            reuniao.setAssunto(reuniaoDTO.getAssunto());
            reuniao.setData(reuniaoDTO.getData());
            reuniao.setHora(reuniaoDTO.getHora());
            reuniao.setFormatoReuniao(reuniaoDTO.getFormatoReuniao());
            reuniao.setProjeto(projeto);
            reuniao.setStatusReuniao(StatusReuniao.PENDENTE);
            reuniao.setSolicitadoPor(reuniaoDTO.getSolicitadoPor());

            reuniaoRepository.save(reuniao);
            return "Solicitação de reunião enviada";
        }

        public String update(long id, Reuniao reuniaoAtualizada) {

            Reuniao reuniaoexiste = reuniaoRepository.findById(id)
                    .orElseThrow(() -> new IllegalStateException("Reunião não encontrada"));

            if(!reuniaoexiste.getStatusReuniao().equals(StatusReuniao.PENDENTE)){
                throw new IllegalStateException("Operação não permitida. A reunião já foi avaliada pelo solicitado");
            }

            if (reuniaoAtualizada.getAssunto() != null) {
                reuniaoexiste.setAssunto(reuniaoAtualizada.getAssunto());
            }
            if (reuniaoAtualizada.getData() != null) {
                reuniaoexiste.setData(reuniaoAtualizada.getData());
            }
            if (reuniaoAtualizada.getHora() != null) {
                reuniaoexiste.setHora(reuniaoAtualizada.getHora());
            }
            if (reuniaoAtualizada.getFormatoReuniao() != null) {
                reuniaoexiste.setFormatoReuniao(reuniaoAtualizada.getFormatoReuniao());
            }
            if (reuniaoAtualizada.getMotivoRecusa() != null){
                reuniaoexiste.setMotivoRecusa(reuniaoAtualizada.getMotivoRecusa());
            }


            reuniaoRepository.save(reuniaoexiste);
            return "Reunião atualizada e reenviada para aprovação";
        }

        public String aceitarReuniao(long id, StatusReuniao novoStatus, String motivo) {

            Reuniao reuniaoExiste = reuniaoRepository.findById(id)
                    .orElseThrow(() -> new IllegalStateException("Reunião não encontrada"));

            reuniaoExiste.setStatusReuniao(novoStatus);

            if(novoStatus == StatusReuniao.RECUSADO){
                reuniaoExiste.setMotivoRecusa(motivo);
            } else {
                reuniaoExiste.setMotivoRecusa(null);
            }

            reuniaoRepository.save(reuniaoExiste);
            return "Status reunião: " + novoStatus.toString().toLowerCase();
        }

        // Deletar reunião
        public String delete(long id) {
            Reuniao reuniao = reuniaoRepository.findById(id).orElseThrow(
                    () -> new IllegalStateException("Reunião não encontrada")
            );

            reuniaoRepository.delete(reuniao);
            return ("Reunião deletada com sucesso");
        }
    }

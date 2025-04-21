package br.edu.fateczl.carometro.model.entity;

import br.edu.fateczl.carometro.model.enums.StatusPostagem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "postagens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Postagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "ex_aluno_id", nullable = false)
    private ExAluno exAluno;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusPostagem status;

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] foto;

    @Column(name = "tipo_foto")
    private String tipoFoto;

    @Column(name = "historico_profissional", columnDefinition = "TEXT")
    private String historicoProfissional;

    @Column(name = "comentario_faculdade", columnDefinition = "TEXT")
    private String comentarioFaculdade;

    @Column(name = "comentario_livre", columnDefinition = "TEXT")
    private String comentarioLivre;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @Column(name = "data_aprovacao")
    private LocalDateTime dataAprovacao;

    @OneToMany(mappedBy = "postagem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LinkPessoal> linksPessoais = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "aprovado_por_id")
    private Usuario aprovadoPor;

    public void adicionarLink(LinkPessoal link) {
        link.setPostagem(this);
        this.linksPessoais.add(link);
    }

    public void removerLink(LinkPessoal link) {
        this.linksPessoais.remove(link);
        link.setPostagem(null);
    }

    @PrePersist
    public void prePersist() {
        this.status = StatusPostagem.PENDENTE;
        this.dataCriacao = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Postagem{" +
                "id=" + id +
                ", exAlunoId=" + (exAluno != null ? exAluno.getId() : null) +
                ", status=" + status +
                ", foto=" + Arrays.toString(foto) +
                ", tipoFoto='" + tipoFoto + '\'' +
                ", historicoProfissional='" + historicoProfissional + '\'' +
                ", comentarioFaculdade='" + comentarioFaculdade + '\'' +
                ", comentarioLivre='" + comentarioLivre + '\'' +
                ", dataCriacao=" + dataCriacao +
                ", dataAtualizacao=" + dataAtualizacao +
                ", dataAprovacao=" + dataAprovacao +
                ", linksPessoais=" + linksPessoais +
                ", aprovadoPor=" + aprovadoPor +
                '}';
    }
}

package br.edu.fateczl.carometro.dto;

public record PostagemFiltroDTO(String curso, Integer anoFormatura) {

    public PostagemFiltroDTO() {
        this(null, null);
    }

    public boolean temCurso() {
        return curso != null && !curso.isEmpty();
    }

    public boolean temAnoFormatura() {
        return anoFormatura != null;
    }
}

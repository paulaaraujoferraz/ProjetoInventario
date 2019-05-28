/*
 * modelo de nota fiscal
 * 
 * 
 */

package br.com.inventario.model;

public class NotaFiscal {
    public String LATO = "00.0000000"; //  Latitude
    public String LONO = "00.0000000"; //  Longitude
    private String id_nota;


    /*INICIO: variaveis para coordenadas do TV*/
    private String numero_nota;
    private String numero_codigobarras;

    private String endereco;
    private String nome;

    private String prioridade;

    private String volumes;



    /*FIM: variaveis para coordenadas do TV*/
    private String empresa;

    private String datahoraleitura;
    //	private String hora_leitura;
//	private String icone;
    private String status;
    private String cor; // usado para verificar se foi inserida com sucesso ou nao pelo webservice


    public NotaFiscal() {
        super();
        // TODO Auto-generated constructor stub
    }

    public String getDatahoraleitura() {
        return datahoraleitura;
    }

    public void setDatahoraleitura(String datahoraleitura) {
        this.datahoraleitura = datahoraleitura;
    }

    public String getId_nota() {
        return id_nota;
    }

    public void setId_nota(String id_nota) {
        this.id_nota = id_nota;
    }

    public String getNumero_nota() {
        return numero_nota;
    }

    public void setNumero_nota(String numero_nota) {
        this.numero_nota = numero_nota;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getNumero_codigobarras() { return numero_codigobarras;  }

    public void setNumero_codigobarras(String numero_codigobarras) {
        this.numero_codigobarras = numero_codigobarras;
    }

    public String getnome() { return nome;  }

    public void setnome(String nome) {
        this.nome = nome;
    }


    public String getNumero_endereco() {  return endereco;    }

    public void setNumero_endereco(String endereco) {
        this.endereco = endereco;
    }


    public String getprioridade() {  return prioridade;    }

    public void setprioridade(String prioridade) {
        this.prioridade = prioridade;
    }

    public String getvolumes() {  return volumes;    }

    public String getdatacarga() {  return datahoraleitura;    }

    public void setvolumes(String volumes) {
        this.volumes = volumes;
    }
    public void setdatacarga(String datahoraleitura) {
        this.datahoraleitura = datahoraleitura;
    }
/* inicio: funcoes para coordenadas de origem */

    public String getLATO() {
        return LATO;
    }

    public void setLATO(String LATO) {
        this.LATO = LATO;
    }


    public String getLONO() {
        return LONO;
    }

    public void setLONO(String LONO) {
        this.LONO = LONO;
    }

 /* fim: funcoes para coordenadas de origem */


}

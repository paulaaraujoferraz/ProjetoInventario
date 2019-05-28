package br.com.inventario;

public class Config {
    // File upload url (replace the ip with your server address)
    //public static final String FILE_UPLOAD_URL = "http://controlog.tempsite.ws/transporteveiculos/v1/ServerPhp/Ctrl/fileUpload.php";
    //public static final String FILE_UPLOAD_URL = "http://env-4434780.jelasticlw.com.br/WScontrologdes/v1/ServerPhp/Ctrl/fileUpload.php";

      /* novo WS a partir de 15/10/2016 para desenvolvimento*/

    //public static final String FILE_UPLOAD_URL = "http://env-4434780.jelasticlw.com.br/WScontrologdesout2016/v1/ServerPhp/Ctrl/fileUpload.php";


  /* novo WS migração controlog a partir de 16/04/2017*/
    //public static final String FILE_UPLOAD_URL = "http://node128429-wscontrolog.jelasticlw.com.br/wscontrolog/v1/fileUpload.php";
     public static final String FILE_UPLOAD_URL = "http://inventario.jelastic.saveincloud.net/wsinventario/v1/foto_recebedor.php";



  public static final String FILE_UPLOAD_TENTATIVA = "http://inventario.jelastic.saveincloud.net/wsinventario/v1/fototentativa.php";
  public static final String FILE_UPLOAD_OCORRENCIA = "http://inventario.jelastic.saveincloud.net/wsinventario/v1/fotosocorrencia.php";



     /* novo WS a partir de 19/11/2016 para producao

    public static final String FILE_UPLOAD_URL = "http://env-4434780.jelasticlw.com.br/WScontrologprodnov16/v1/ServerPhp/Ctrl/fileUpload.php";

*/


    /* criar WS para transmitir localização de tempos em tempos*/
  //  public static final String FILE_UPLOAD_URL = "http://env-4434780.jelasticlw.com.br/levejamobiletvprod/v1/ServerPhp/Ctrl/fileUpload.php";


    // Directory name to store captured images and videos
    public static final String IMAGE_DIRECTORY_NAME = "img";
}

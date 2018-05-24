package rafanereslima.com.br.cedroteste.mDataBase;

public class Constants {
    //DATA
    static final String ROW_ID="id";
    static final String SITE="site";
    static final String PASSWORD="password";



    //DB PROPRIEDADES
    static final String DB_NAME="hh_DB";
    static final String TB_NAME="hh_TB";
    static final int DB_VERSION=1;

    //CRIANDO DB
    static final String CREATE_TB="CREATE TABLE hh_TB(id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "site TEXT NOT NULL," + "password TEXT NOT NULL);";


    //DROP TB
    static final String DROP_TB="DROP TABLE IF EXISTS "+TB_NAME;
}

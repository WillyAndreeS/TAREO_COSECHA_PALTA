package com.acpagro.tareopalta.datos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLite  extends SQLiteOpenHelper {
    private static String nombreBD = "bd_tareo_acp_palta";
    //private static int versionBD = 8;//22/08/2019
    //private static int versionBD = 9;//28/08/2019
    //private static int versionBD = 11;//10/09/2019
    //private static int versionBD = 13;//07/04/2020
    //private static int versionBD = 15;//23/02/2021
    //private static int versionBD = 16;//29/03/2021
    //private static int versionBD = 17;//04/04/2021
    //private static int versionBD = 18;//23/06/2021
    //private static int versionBD = 19;//07/02/2022
    private static int versionBD = 20;//07/04/2022

    public static Context aplicacion;//Permite referenciar la aplicación donde se instalará la BD
    public static String tablaUsuario = "CREATE TABLE USUARIO(IDUSUARIO INT PRIMARY KEY, DNI VARCHAR(8), CLAVE VARCHAR(100), IDCULTIVO VARCHAR(20), LEE_PDA VARCHAR(10), TIPO_USUARIO VARCHAR(50))";
    public static String tablaPersonalGeneral = "CREATE TABLE PERSONALGENERAL(CODIGO VARCHAR(20) PRIMARY KEY, DNI VARCHAR(8), NOMBRES VARCHAR(200), OBSERVADO VARCHAR(10), MODULO VARCHAR(50), MOTIVO VARCHAR(200))";//OBSERVADO (SI O NO)
    public static String tablaLabor = "CREATE TABLE LABOR(IDEMPRESA VARCHAR(50), IDACTIVIDAD VARCHAR(10), IDLABOR VARCHAR(10), LABOR VARCHAR (100), ALIAS VARCHAR(100))";
    public static String tablaSubLabor = "CREATE TABLE SUBLABOR(IDACTIVIDAD VARCHAR(5), IDLABOR VARCHAR(5), IDSUBLABOR VARCHAR(5), SUBLABOR VARCHAR (100), GRUPO VARCHAR(2), UNIDAD VARCHAR(5))";
    public static String tablaActividades = "CREATE TABLE ACTIVIDAD(IDEMPRESA VARCHAR(50), IDACTIVIDAD VARCHAR(10), ACTIVIDAD VARCHAR(100), ALIAS VARCHAR(50))";
    public static String tablaConsumidor = "CREATE TABLE CONSUMIDOR(IDEMPRESA VARCHAR(50), IDCONSUMIDOR VARCHAR(50), CONSUMIDOR VARCHAR(200), IDCULTIVO VARCHAR(20), CULTIVO VARCHAR(50))";
    public static String tablaValvulas = "CREATE TABLE VALVULA(IDVALVULA VARCHAR(50), DESCRIPCION VARCHAR(100), IDCONSUMIDOR VARCHAR(50))";
    public static String tablaTareo = "CREATE TABLE TAREO(IDTAREO VARCHAR(50) PRIMARY KEY, FECHA TIMESTAMP DEFAULT(STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW')), IDUSUARIO INT, IDCONSUMIDOR VARCHAR(50), IDACTIVIDAD VARCHAR(10), IDLABOR VARCHAR(10), IDSUBLABOR VARCHAR(10), TIPOLABOR CHAR(1), HORAS NUMERIC(10,2) DEFAULT 9.5, SINCRONIZADO CHAR(1) DEFAULT '0', OBSERVACION VARCHAR(150), IDTURNO CHAR(2), IDGRUPO VARCHAR(50))";
    public static String tablaTicketPersona = "CREATE TABLE TICKETPERSONA(IDTICKET VARCHAR(50), IDTAREO VARCHAR(50), IDPERSONALGENERAL VARCHAR(20), FECHAREGISTRO DATETIME DEFAULT CURRENT_TIMESTAMP, SINCRONIZADO CHAR(1) DEFAULT '0', ID_REGISTRA CHAR(8), FECHAMINGUILLO DATE DEFAULT CURRENT_DATE, UNIQUE(IDTICKET, IDPERSONALGENERAL, FECHAMINGUILLO))";
    //public static String tablaTicketCosecha = "CREATE TABLE TICKETCOSECHA(IDTICKET VARCHAR(50), IDTAREO VARCHAR(50), IDCONSUMIDOR VARCHAR(50), IDVALVULA VARCHAR(20), FECHAREGISTRO DATETIME DEFAULT CURRENT_TIMESTAMP, SINCRONIZADO CHAR(1) DEFAULT '0', IDVIAJE VARCHAR(20), VARIEDAD VARCHAR(20), KG_DESMEDRO NUMERIC(10,2), ID_REGISTRA INT)";
    public static String tablaTicketCosecha = "CREATE TABLE TICKETCOSECHA(IDTICKET VARCHAR(50), IDTAREO VARCHAR(50), IDCONSUMIDOR VARCHAR(50), IDVALVULA VARCHAR(20), FECHAREGISTRO TIMESTAMP DEFAULT(STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW')), SINCRONIZADO CHAR(1) DEFAULT '0', IDVIAJE VARCHAR(20), VARIEDAD VARCHAR(20), KG_DESMEDRO NUMERIC(10,2), ID_REGISTRA INT, CODIGO VARCHAR(100), UNIQUE(CODIGO))";
    public static String tablaDetalleTareo = "CREATE TABLE DETALLETAREO(IDTAREO VARCHAR(50), IDPERSONALGENERAL VARCHAR(8), RENDIMIENTO NUMERIC(10,2) DEFAULT 0, IDSUBLABOR CHAR(3) DEFAULT 0, FECHAREGISTRO TIMESTAMP DEFAULT(STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW')), SINCRONIZADO CHAR(1) DEFAULT '0', IDVALVULA VARCHAR(50), IDCONSUMIDOR VARCHAR(50))";
    public static String tablaJabero = "CREATE TABLE JABERO(DNI CHAR(8), FECHA DATE DEFAULT CURRENT_DATE, FECHAREGISTRO DATETIME DEFAULT CURRENT_TIMESTAMP, SINCRONIZADO CHAR(1) DEFAULT '0', ID_REGISTRA INTEGER)";
    //Asistecia, las anteriores eran de Tareo
    //public static String tablaAsistencia = "CREATE TABLE ASISTENCIA(IDTAREO VARCHAR(50), IDPERSONALGENERAL VARCHAR(20), FECHAREGISTRO DATETIME DEFAULT CURRENT_TIMESTAMP, SINCRONIZADO CHAR(1) DEFAULT '0', UNIQUE(IDTAREO, IDPERSONALGENERAL))";
    public static String tablaAsistencia = "CREATE TABLE ASISTENCIA(IDTAREO VARCHAR(50), IDPERSONALGENERAL VARCHAR(20), FECHAREGISTRO TIMESTAMP DEFAULT(STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW')), SINCRONIZADO CHAR(1) DEFAULT '0', UNIQUE(IDTAREO, IDPERSONALGENERAL))";
    public static String tablaSalidaPersonal = "CREATE TABLE SALIDAPERSONAL(DNI VARCHAR(8), FECHA DATE DEFAULT CURRENT_DATE, HORA VARCHAR(50), FECHAREGISTRO DATETIME DEFAULT CURRENT_TIMESTAMP, SINCRONIZADO CHAR(1) DEFAULT '0', PRIMARY KEY (DNI, FECHA))";
    public static String tablaHorasPersonal = "CREATE TABLE HORAPERSONAL(IDTAREO VARCHAR(50), DNI VARCHAR(8), HORAS NUMERIC(10,2), FECHAREGISTRO DATETIME DEFAULT CURRENT_TIMESTAMP, SINCRONIZADO CHAR(1) DEFAULT '0', PRIMARY KEY (IDTAREO, DNI))";
    public static String tablaAdministrador = "CREATE TABLE ADMINISTRADOR(NUMERO VARCHAR(50) PRIMARY KEY, CORREO VARCHAR(100), NOMBRE VARCHAR(200))";

    public static String tablaDefectos = "CREATE TABLE DEFECTOS(IDDEFECTO INT PRIMARY KEY, IDCULTIVO CHAR(4), NOMBRE VARCHAR(100), DESCRIPCION VARCHAR(100))";
    public static String tablaRegistroDefectos = "CREATE TABLE REGISTRODEFECTOS(IDREGISTRODEFECTO integer primary key AUTOINCREMENT, IDTICKET VARCHAR(20), IDDEFECTO INT, IDJABERO VARCHAR(8), IDACOPIADOR VARCHAR(8), IDCOSECHADOR VARCHAR(8), OBSERVACION VARCHAR(200), FECHAREGISTRO DATETIME DEFAULT CURRENT_TIMESTAMP, SINCRONIZADO CHAR(1) DEFAULT '0')";
    //public static String tablaAsistencia = "CREATE TABLE ASISTENCIA(IDTAREO VARCHAR(50), IDPERSONALGENERAL VARCHAR(20), FECHAREGISTRO DATETIME DEFAULT CURRENT_TIMESTAMP, SINCRONIZADO CHAR(1) DEFAULT '0', UNIQUE(IDTAREO, IDPERSONALGENERAL) ON CONFLICT REPLACE)";

    public static String tablaHoraTareo = "CREATE TABLE HORATAREO(IDCULTIVO VARCHAR (10), DIA VARCHAR(30), HORAS NUMERIC(10,2), FECHAREGISTRO VARCHAR(100), IDUSUARIO VARCHAR(30))";
    //public static String tablaParaResumenLectura = "CREATE TABLE RESLECTURA(FECHA DATE, IDCONSUMIDOR VARCHAR(50), CONSUMIDOR VARCHAR(50), IDCODIGO VARCHAR(50))";
    public static String tablaTareoCampoViajes = "CREATE TABLE TAREOCAMPO_VIAJES(IDCULTIVO CHAR(4), IDVIAJE VARCHAR(20), PLACA VARCHAR(20), NROJABAS INT, IDUSUARIO INT, FECHAREGISTRO DATETIME DEFAULT CURRENT_TIMESTAMP, SINCRONIZADO CHAR(1) DEFAULT '0')";
    public static String tablaVariedad = "CREATE TABLE VARIEDAD_CULTIVO(IDCULTIVO CHAR(4), IDVARIEDAD VARCHAR(3), DESCRIPCION VARCHAR(50))";

    public static String tablaGrupo = "CREATE TABLE GRUPO(IDGRUPO VARCHAR(50) PRIMARY KEY, NUM_GRUPO_DESC VARCHAR(20), NUMERO INTEGER, COPIA_DE_ID VARCHAR(50), COPIA_DE_DESC VARCHAR(20), FECHAREGISTRO DATETIME, ESTADO BIT DEFAULT 1)";

    public static String tablaBinesCabecera = "CREATE TABLE VIAJE_BINES_CABECERA(IDCABECERA VARCHAR(50) PRIMARY KEY, PLACA VARCHAR(50), CHOFER VARCHAR(200), IDCULTIVO CHAR(4), IDUSUARIO INT, FECHAREGISTRO DATETIME, ESTADO BIT DEFAULT 1, SINCRONIZADO CHAR(1) DEFAULT '0', IDTELEFONO VARCHAR(50), UNIDAD_COD_SIS1 VARCHAR(20), UNIDAD_COD_SIS2 VARCHAR(20))";
    public static String tablaBinesDetalle = "CREATE TABLE VIAJE_BINES_DETALLE(IDDETALLE VARCHAR(50) PRIMARY KEY, IDCABECERA VARCHAR(50), IDBIN VARCHAR(50), IDCONSUMIDOR VARCHAR(50), IDVARIEDAD VARCHAR(10), IDVALVULA VARCHAR(10), IDUSUARIO INT, FECHAREGISTRO DATETIME, ESTADO BIT DEFAULT 1, SINCRONIZADO CHAR(1) DEFAULT '0', IDTELEFONO VARCHAR(50), UNIQUE(IDCABECERA, IDBIN))";
    public static String tablaLeturaMaquinaria = "CREATE TABLE LECTURA_MAQUINARIA(IDPORTABIN VARCHAR(50), IDTRAZABILIDAD VARCHAR(50), IDBIN VARCHAR(50), IDUSUARIO INT, FECHAREGISTRO DATETIME, ESTADO BIT DEFAULT 1, SINCRONIZADO CHAR(1) DEFAULT '0', IDTELEFONO VARCHAR(50));";
    public static String tablaSalidaAsistencia = "CREATE TABLE SALIDA_ASISTENCIA(DNI VARCHAR(8), " +
            "FECHA DATETIME, IDUSUARIO INT, FECHAREGISTRO DATETIME, ESTADO BIT DEFAULT 1, SINCRONIZADO CHAR(1) DEFAULT '0', IDTELEFONO VARCHAR(50), PRIMARY KEY(DNI, FECHA) );";
    public static String tablaReconocimientoFacial = "CREATE TABLE RECOGNITION(IDCODIGOGENERAL VARCHAR(8) PRIMARY KEY, DISTANCE NUMERIC(10, 2), EXTRA_FACE VARCHAR(10000), ID_OPTIONAL VARCHAR(200), TITLE_OPTIONAL VARCHAR(200), FECHAREGISTRO DATETIME, SINCRONIZADO CHAR(1) DEFAULT '0')";

    public static String eliminarTablaUsuario = "drop table if exists USUARIO";
    public static String eliminarTablaValvula = "drop table if exists VALVULA";
    public static String eliminarTablaPersonalGeneral = "drop table if exists PERSONALGENERAL";
    public static String eliminarTablaLabor = "drop table if exists LABOR";
    public static String eliminarTablaSubLabor = "drop table if exists SUBLABOR";
    public static String eliminarTablaActividades = "drop table if exists ACTIVIDAD";
    public static String eliminarTablaConsumidor = "drop table if exists CONSUMIDOR";
    public static String eliminarTablaTareo = "drop table if exists TAREO";
    public static String eliminarTablaTicketPersona = "drop table if exists TICKETPERSONA";
    public static String eliminarTablaTicketCosecha = "drop table if exists TICKETCOSECHA";
    public static String eliminarTablaDTAREO = "drop table if exists DETALLETAREO";
    public static String eliminarTablaASISTENCIA = "drop table if exists ASISTENCIA";
    public static String eliminarTablaSalidaPersonal = "drop table if exists SALIDAPERSONAL";
    public static String eliminarTablaHorasPersonal = "drop table if exists HORAPERSONAL";
    public static String eliminarTablaAdministradores = "drop table if exists ADMINISTRADOR";
    public static String eliminartablaHoraTareo = "drop table if exists HORATAREO";
    public static String eliminarTablaDefectos = "drop table if exists DEFECTOS";
    public static String eliminarTablaREGISTRODEFECTOS = "drop table if exists REGISTRODEFECTOS";
    public static String eliminarTablaJABERO = "drop table if exists JABERO";
    public static String eliminarTablaTareoCampoViajes = "drop table if exists TAREOCAMPO_VIAJES";
    public static String eliminarTablatablaVariedad = "drop table if exists VARIEDAD_CULTIVO";
    public static String eliminarTablaGrupo = "drop table if exists GRUPO";
    public static String eliminarTablaBinesCabecera = "drop table if exists VIAJE_BINES_CABECERA";
    public static String eliminarTablaBinesDetalle = "drop table if exists VIAJE_BINES_DETALLE";
    public static String eliminartablaLeturaMaquinaria = "drop table if exists LECTURA_MAQUINARIA";
    public static String eliminartablaSalidaAsistencia = "drop table if exists SALIDA_ASISTENCIA";
    public static String eliminartablaReconocimiento = "drop table if exists RECOGNITION";

    private static String insertUsuariosPorDefecto[]={
            "insert into USUARIO values (0, '00000000', 'admin', '0021', 'NO', 'NORMAL');"
    };

    private static String insertPersonalPorDefecto[]={
            "insert into PERSONALGENERAL values ('00000000', '00000000', 'ADMINISTRADOR GENERAL', 'NO', 'MODULO_04', 'NINGUNO');"
    };

    public SQLite() {
        super(aplicacion, nombreBD, null, versionBD);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(tablaUsuario);
        db.execSQL(tablaValvulas);
        db.execSQL(tablaPersonalGeneral);
        db.execSQL(tablaLabor);
        db.execSQL(tablaSubLabor);
        db.execSQL(tablaActividades);
        db.execSQL(tablaConsumidor);
        db.execSQL(tablaTareo);
        db.execSQL(tablaAsistencia);
        db.execSQL(tablaTicketPersona);
        db.execSQL(tablaTicketCosecha);
        db.execSQL(tablaDetalleTareo);
        db.execSQL(tablaSalidaPersonal);
        db.execSQL(tablaHorasPersonal);
        db.execSQL(tablaAdministrador);
        db.execSQL(tablaHoraTareo);
        db.execSQL(tablaDefectos);
        db.execSQL(tablaRegistroDefectos);
        db.execSQL(tablaJabero);
        db.execSQL(tablaTareoCampoViajes);//27/08/2019
        db.execSQL(tablaVariedad);//27/08/2019
        db.execSQL(tablaGrupo);//27/08/2019
        db.execSQL(tablaBinesCabecera);//23/02/2021
        db.execSQL(tablaBinesDetalle);//23/02/2021
        db.execSQL(tablaLeturaMaquinaria);//04/04/2021
        db.execSQL(tablaSalidaAsistencia);//23/06/2021
        db.execSQL(tablaReconocimientoFacial);//07/02/2022

        for (int i=0; i<insertUsuariosPorDefecto.length;i++){
            db.execSQL(insertUsuariosPorDefecto[i]);
        }
        for (int i=0; i<insertPersonalPorDefecto.length;i++){
            db.execSQL(insertPersonalPorDefecto[i]);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*db.execSQL(eliminarTablaUsuario);
        db.execSQL(eliminarTablaValvula);
        db.execSQL(eliminarTablaPersonalGeneral);
        db.execSQL(eliminarTablaLabor);
        db.execSQL(eliminarTablaSubLabor);
        db.execSQL(eliminarTablaActividades);
        db.execSQL(eliminarTablaConsumidor);
        db.execSQL(eliminarTablaTareo);
        db.execSQL(eliminarTablaASISTENCIA);
        db.execSQL(eliminarTablaTicketPersona);

        db.execSQL(eliminarTablaDTAREO);
        db.execSQL(eliminarTablaSalidaPersonal);
        db.execSQL(eliminarTablaHorasPersonal);
        db.execSQL(eliminarTablaAdministradores);
        db.execSQL(eliminartablaHoraTareo);
        db.execSQL(eliminarTablaDefectos);
        db.execSQL(eliminarTablaREGISTRODEFECTOS);
        db.execSQL(eliminarTablaJABERO);
        db.execSQL(eliminarTablaTareoCampoViajes);
        db.execSQL(eliminarTablatablaVariedad);
        db.execSQL(eliminarTablaGrupo);
        db.execSQL(eliminarTablaBinesCabecera);
        db.execSQL(eliminarTablaBinesDetalle);
        db.execSQL(eliminartablaLeturaMaquinaria);*/
        /*
        //20220908
        db.execSQL(eliminarTablaTicketCosecha);
        db.execSQL(eliminartablaSalidaAsistencia);
        db.execSQL(eliminartablaReconocimiento);
        //20220908
        */

        /*db.execSQL(tablaUsuario);
        db.execSQL(tablaValvulas);
        db.execSQL(tablaPersonalGeneral);
        db.execSQL(tablaLabor);
        db.execSQL(tablaSubLabor);
        db.execSQL(tablaActividades);
        db.execSQL(tablaConsumidor);
        db.execSQL(tablaTareo);
        db.execSQL(tablaAsistencia);
        db.execSQL(tablaTicketPersona);

        db.execSQL(tablaDetalleTareo);
        db.execSQL(tablaSalidaPersonal);
        db.execSQL(tablaHorasPersonal);
        db.execSQL(tablaAdministrador);
        db.execSQL(tablaHoraTareo);
        db.execSQL(tablaDefectos);
        db.execSQL(tablaRegistroDefectos);
        db.execSQL(tablaJabero);
        db.execSQL(tablaTareoCampoViajes);//27/08/2019
        db.execSQL(tablaVariedad);//27/08/2019
        db.execSQL(tablaGrupo);
        db.execSQL(tablaBinesCabecera);//23/02/2021
        db.execSQL(tablaBinesDetalle);//23/02/2021
        db.execSQL(tablaLeturaMaquinaria);//04/04/2021
        */

        /*
        //20220908
        db.execSQL(tablaTicketCosecha);
        db.execSQL(tablaSalidaAsistencia);//23/06/2021
        db.execSQL(tablaReconocimientoFacial);//07/02/2022
        //20220908
        */

        /*
        for (int i=0; i<insertUsuariosPorDefecto.length;i++){
            db.execSQL(insertUsuariosPorDefecto[i]);
        }
        for (int i=0; i<insertPersonalPorDefecto.length;i++){
            db.execSQL(insertPersonalPorDefecto[i]);
        }

        */

    }
}

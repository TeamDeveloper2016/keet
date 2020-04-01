/*
SQLyog Community v12.2.4 (64 bit)
MySQL - 10.1.16-MariaDB : Database - keet
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`keet` /*!40100 DEFAULT CHARACTER SET latin1 COLLATE latin1_bin */;

USE `keet`;

/*Table structure for table `tc_keet_estaciones` */

DROP TABLE IF EXISTS `tc_keet_estaciones`;

CREATE TABLE `tc_keet_estaciones` (
  `id_estacion` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
  `id_plantilla` bigint(11) unsigned NOT NULL DEFAULT '2',
  `nivel` bigint(11) NOT NULL DEFAULT '1',
  `clave` varchar(250) COLLATE latin1_bin NOT NULL,
  `codigo` varchar(100) COLLATE latin1_bin DEFAULT NULL,
  `nombre` varchar(255) COLLATE latin1_bin NOT NULL,
  `descripcion` varchar(500) COLLATE latin1_bin DEFAULT NULL,
  `id_empaque_unidad_medida` bigint(11) unsigned DEFAULT NULL,
  `inicio` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `termino` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `cantidad` decimal(11,4) NOT NULL DEFAULT '0.0000',
  `costo` decimal(11,4) NOT NULL DEFAULT '0.0000',
  `ultimo` bigint(11) unsigned NOT NULL DEFAULT '2',
  `cargo_1` decimal(11,4) DEFAULT '0.0000',
  `abono_1` decimal(11,4) DEFAULT '0.0000',
  `acumulado_1` decimal(11,4) DEFAULT '0.0000',
  `cargo_2` decimal(11,4) DEFAULT '0.0000',
  `abono_2` decimal(11,4) DEFAULT '0.0000',
  `acumulado_2` decimal(11,4) DEFAULT '0.0000',
  `cargo_3` decimal(11,4) DEFAULT '0.0000',
  `abono_3` decimal(11,4) DEFAULT '0.0000',
  `acumulado_3` decimal(11,4) DEFAULT '0.0000',
  `cargo_4` decimal(11,4) DEFAULT '0.0000',
  `abono_4` decimal(11,4) DEFAULT '0.0000',
  `acumulado_4` decimal(11,4) DEFAULT '0.0000',
  `cargo_5` decimal(11,4) DEFAULT '0.0000',
  `abono_5` decimal(11,4) DEFAULT '0.0000',
  `acumulado_5` decimal(11,4) DEFAULT '0.0000',
  `cargo_6` decimal(11,4) DEFAULT '0.0000',
  `abono_6` decimal(11,4) DEFAULT '0.0000',
  `acumulado_6` decimal(11,4) DEFAULT '0.0000',
  `cargo_7` decimal(11,4) DEFAULT '0.0000',
  `abono_7` decimal(11,4) DEFAULT '0.0000',
  `acumulado_7` decimal(11,4) DEFAULT '0.0000',
  `cargo_8` decimal(11,4) DEFAULT '0.0000',
  `abono_8` decimal(11,4) DEFAULT '0.0000',
  `acumulado_8` decimal(11,4) DEFAULT '0.0000',
  `cargo_9` decimal(11,4) DEFAULT '0.0000',
  `abono_9` decimal(11,4) DEFAULT '0.0000',
  `acumulado_9` decimal(11,4) DEFAULT '0.0000',
  `cargo_10` decimal(11,4) DEFAULT '0.0000',
  `abono_10` decimal(11,4) DEFAULT '0.0000',
  `acumulado_10` decimal(11,4) DEFAULT '0.0000',
  `cargo_11` decimal(11,4) DEFAULT '0.0000',
  `abono_11` decimal(11,4) DEFAULT '0.0000',
  `acumulado_11` decimal(11,4) DEFAULT '0.0000',
  `cargo_12` decimal(11,4) DEFAULT '0.0000',
  `abono_12` decimal(11,4) DEFAULT '0.0000',
  `acumulado_12` decimal(11,4) DEFAULT '0.0000',
  `cargo_13` decimal(11,4) DEFAULT '0.0000',
  `abono_13` decimal(11,4) DEFAULT '0.0000',
  `acumulado_13` decimal(11,4) DEFAULT '0.0000',
  `cargo_14` decimal(11,4) DEFAULT '0.0000',
  `abono_14` decimal(11,4) DEFAULT '0.0000',
  `acumulado_14` decimal(11,4) DEFAULT '0.0000',
  `cargo_15` decimal(11,4) DEFAULT '0.0000',
  `abono_15` decimal(11,4) DEFAULT '0.0000',
  `acumulado_15` decimal(11,4) DEFAULT '0.0000',
  `cargo_16` decimal(11,4) DEFAULT '0.0000',
  `abono_16` decimal(11,4) DEFAULT '0.0000',
  `acumulado_16` decimal(11,4) DEFAULT '0.0000',
  `cargo_17` decimal(11,4) DEFAULT '0.0000',
  `abono_17` decimal(11,4) DEFAULT '0.0000',
  `acumulado_17` decimal(11,4) DEFAULT '0.0000',
  `cargo_18` decimal(11,4) DEFAULT '0.0000',
  `abono_18` decimal(11,4) DEFAULT '0.0000',
  `acumulado_18` decimal(11,4) DEFAULT '0.0000',
  `cargo_19` decimal(11,4) DEFAULT '0.0000',
  `abono_19` decimal(11,4) DEFAULT '0.0000',
  `acumulado_19` decimal(11,4) DEFAULT '0.0000',
  `cargo_20` decimal(11,4) DEFAULT '0.0000',
  `abono_20` decimal(11,4) DEFAULT '0.0000',
  `acumulado_20` decimal(11,4) DEFAULT '0.0000',
  `cargo_21` decimal(11,4) DEFAULT '0.0000',
  `abono_21` decimal(11,4) DEFAULT '0.0000',
  `acumulado_21` decimal(11,4) DEFAULT '0.0000',
  `cargo_22` decimal(11,4) DEFAULT '0.0000',
  `abono_22` decimal(11,4) DEFAULT '0.0000',
  `acumulado_22` decimal(11,4) DEFAULT '0.0000',
  `cargo_23` decimal(11,4) DEFAULT '0.0000',
  `abono_23` decimal(11,4) DEFAULT '0.0000',
  `acumulado_23` decimal(11,4) DEFAULT '0.0000',
  `cargo_24` decimal(11,4) DEFAULT '0.0000',
  `abono_24` decimal(11,4) DEFAULT '0.0000',
  `acumulado_24` decimal(11,4) DEFAULT '0.0000',
  `cargo_25` decimal(11,4) DEFAULT '0.0000',
  `abono_25` decimal(11,4) DEFAULT '0.0000',
  `acumulado_25` decimal(11,4) DEFAULT '0.0000',
  `cargo_26` decimal(11,4) DEFAULT '0.0000',
  `abono_26` decimal(11,4) DEFAULT '0.0000',
  `acumulado_26` decimal(11,4) DEFAULT '0.0000',
  `cargo_27` decimal(11,4) DEFAULT '0.0000',
  `abono_27` decimal(11,4) DEFAULT '0.0000',
  `acumulado_27` decimal(11,4) DEFAULT '0.0000',
  `cargo_28` decimal(11,4) DEFAULT '0.0000',
  `abono_28` decimal(11,4) DEFAULT '0.0000',
  `acumulado_28` decimal(11,4) DEFAULT '0.0000',
  `cargo_29` decimal(11,4) DEFAULT '0.0000',
  `abono_29` decimal(11,4) DEFAULT '0.0000',
  `acumulado_29` decimal(11,4) DEFAULT '0.0000',
  `cargo_30` decimal(11,4) DEFAULT '0.0000',
  `abono_30` decimal(11,4) DEFAULT '0.0000',
  `acumulado_30` decimal(11,4) DEFAULT '0.0000',
  `cargo_31` decimal(11,4) DEFAULT '0.0000',
  `abono_31` decimal(11,4) DEFAULT '0.0000',
  `acumulado_31` decimal(11,4) DEFAULT '0.0000',
  `cargo_32` decimal(11,4) DEFAULT '0.0000',
  `abono_32` decimal(11,4) DEFAULT '0.0000',
  `acumulado_32` decimal(11,4) DEFAULT '0.0000',
  `cargo_33` decimal(11,4) DEFAULT '0.0000',
  `abono_33` decimal(11,4) DEFAULT '0.0000',
  `acumulado_33` decimal(11,4) DEFAULT '0.0000',
  `cargo_34` decimal(11,4) DEFAULT '0.0000',
  `abono_34` decimal(11,4) DEFAULT '0.0000',
  `acumulado_34` decimal(11,4) DEFAULT '0.0000',
  `cargo_35` decimal(11,4) DEFAULT '0.0000',
  `abono_35` decimal(11,4) DEFAULT '0.0000',
  `acumulado_35` decimal(11,4) DEFAULT '0.0000',
  `cargo_36` decimal(11,4) DEFAULT '0.0000',
  `abono_36` decimal(11,4) DEFAULT '0.0000',
  `acumulado_36` decimal(11,4) DEFAULT '0.0000',
  `cargo_37` decimal(11,4) DEFAULT '0.0000',
  `abono_37` decimal(11,4) DEFAULT '0.0000',
  `acumulado_37` decimal(11,4) DEFAULT '0.0000',
  `cargo_38` decimal(11,4) DEFAULT '0.0000',
  `abono_38` decimal(11,4) DEFAULT '0.0000',
  `acumulado_38` decimal(11,4) DEFAULT '0.0000',
  `cargo_39` decimal(11,4) DEFAULT '0.0000',
  `abono_39` decimal(11,4) DEFAULT '0.0000',
  `acumulado_39` decimal(11,4) DEFAULT '0.0000',
  `cargo_40` decimal(11,4) DEFAULT '0.0000',
  `abono_40` decimal(11,4) DEFAULT '0.0000',
  `acumulado_40` decimal(11,4) DEFAULT '0.0000',
  `cargo_41` decimal(11,4) DEFAULT '0.0000',
  `abono_41` decimal(11,4) DEFAULT '0.0000',
  `acumulado_41` decimal(11,4) DEFAULT '0.0000',
  `cargo_42` decimal(11,4) DEFAULT '0.0000',
  `abono_42` decimal(11,4) DEFAULT '0.0000',
  `acumulado_42` decimal(11,4) DEFAULT '0.0000',
  `cargo_43` decimal(11,4) DEFAULT '0.0000',
  `abono_43` decimal(11,4) DEFAULT '0.0000',
  `acumulado_43` decimal(11,4) DEFAULT '0.0000',
  `cargo_44` decimal(11,4) DEFAULT '0.0000',
  `abono_44` decimal(11,4) DEFAULT '0.0000',
  `acumulado_44` decimal(11,4) DEFAULT '0.0000',
  `cargo_45` decimal(11,4) DEFAULT '0.0000',
  `abono_45` decimal(11,4) DEFAULT '0.0000',
  `acumulado_45` decimal(11,4) DEFAULT '0.0000',
  `cargo_46` decimal(11,4) DEFAULT '0.0000',
  `abono_46` decimal(11,4) DEFAULT '0.0000',
  `acumulado_46` decimal(11,4) DEFAULT '0.0000',
  `cargo_47` decimal(11,4) DEFAULT '0.0000',
  `abono_47` decimal(11,4) DEFAULT '0.0000',
  `acumulado_47` decimal(11,4) DEFAULT '0.0000',
  `cargo_48` decimal(11,4) DEFAULT '0.0000',
  `abono_48` decimal(11,4) DEFAULT '0.0000',
  `acumulado_48` decimal(11,4) DEFAULT '0.0000',
  `cargo_49` decimal(11,4) DEFAULT '0.0000',
  `abono_49` decimal(11,4) DEFAULT '0.0000',
  `acumulado_49` decimal(11,4) DEFAULT '0.0000',
  `cargo_50` decimal(11,4) DEFAULT '0.0000',
  `abono_50` decimal(11,4) DEFAULT '0.0000',
  `acumulado_50` decimal(11,4) DEFAULT '0.0000',
  `cargo_51` decimal(11,4) DEFAULT '0.0000',
  `abono_51` decimal(11,4) DEFAULT '0.0000',
  `acumulado_51` decimal(11,4) DEFAULT '0.0000',
  `cargo_52` decimal(11,4) DEFAULT '0.0000',
  `abono_52` decimal(11,4) DEFAULT '0.0000',
  `acumulado_52` decimal(11,4) DEFAULT '0.0000',
  `cargo_53` decimal(11,4) DEFAULT '0.0000',
  `abono_53` decimal(11,4) DEFAULT '0.0000',
  `acumulado_53` decimal(11,4) DEFAULT '0.0000',
  `cargo_54` decimal(11,4) DEFAULT '0.0000',
  `abono_54` decimal(11,4) DEFAULT '0.0000',
  `acumulado_54` decimal(11,4) DEFAULT '0.0000',
  `cargo_55` decimal(11,4) DEFAULT '0.0000',
  `abono_55` decimal(11,4) DEFAULT '0.0000',
  `acumulado_55` decimal(11,4) DEFAULT '0.0000',
  `id_estacion_estatus` bigint(11) unsigned NOT NULL,
  `id_usuario` bigint(11) unsigned NOT NULL,
  `registro` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_estacion`),
  KEY `id_usuario` (`id_usuario`),
  KEY `clave_uk` (`id_plantilla`,`clave`),
  KEY `id_plantilla` (`id_plantilla`),
  KEY `id_empaque_unidad_medida` (`id_empaque_unidad_medida`),
  KEY `id_estacion_estatus` (`id_estacion_estatus`),
  CONSTRAINT `tc_keet_estaciones_ibfk_1` FOREIGN KEY (`id_plantilla`) REFERENCES `tc_janal_booleanos` (`id_booleano`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `tc_keet_estaciones_ibfk_2` FOREIGN KEY (`id_empaque_unidad_medida`) REFERENCES `tr_mantic_empaque_unidad_medida` (`id_empaque_unidad_medida`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `tc_keet_estaciones_ibfk_3` FOREIGN KEY (`id_usuario`) REFERENCES `tc_janal_usuarios` (`id_usuario`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=latin1 COLLATE=latin1_bin;

/*Data for the table `tc_keet_estaciones` */

LOCK TABLES `tc_keet_estaciones` WRITE;

insert  into `tc_keet_estaciones`(`id_estacion`,`id_plantilla`,`nivel`,`clave`,`codigo`,`nombre`,`descripcion`,`id_empaque_unidad_medida`,`inicio`,`termino`,`cantidad`,`costo`,`ultimo`,`cargo_1`,`abono_1`,`acumulado_1`,`id_estacion_estatus`,`id_usuario`,`registro`) values 
(1,2,1,'',NULL,'PREELIMINARES',NULL,NULL,'2020-04-01 14:44:55','2020-04-01 14:44:55','0.0000','0.0000',2,'0.0000','0.0000','0.0000',0,1,'2020-02-27 13:29:07'),
(2,2,1,'',NULL,'CIMENTACION',NULL,NULL,'2020-04-01 14:44:55','2020-04-01 14:44:55','0.0000','0.0000',2,'0.0000','0.0000','0.0000',0,1,'2020-02-28 07:50:55'),
(3,2,1,'',NULL,'MUROS PLANTA BAJA',NULL,NULL,'2020-04-01 14:44:55','2020-04-01 14:44:55','0.0000','0.0000',2,'0.0000','0.0000','0.0000',0,1,'2020-02-28 07:54:51'),
(4,2,1,'',NULL,'CASTILLOS PLANTA BAJA',NULL,NULL,'2020-04-01 14:44:55','2020-04-01 14:44:55','0.0000','0.0000',2,'0.0000','0.0000','0.0000',0,1,'2020-02-28 07:54:51'),
(5,2,1,'',NULL,'LOSA DE ENTREPISO',NULL,NULL,'2020-04-01 14:44:55','2020-04-01 14:44:55','0.0000','0.0000',2,'0.0000','0.0000','0.0000',0,1,'2020-02-28 07:54:51'),
(6,2,1,'',NULL,'INSTALACIONES',NULL,NULL,'2020-04-01 14:44:55','2020-04-01 14:44:55','0.0000','0.0000',2,'0.0000','0.0000','0.0000',0,1,'2020-02-28 07:54:51'),
(7,2,1,'',NULL,'MUROS PLANTA ALTA',NULL,NULL,'2020-04-01 14:44:55','2020-04-01 14:44:55','0.0000','0.0000',2,'0.0000','0.0000','0.0000',0,1,'2020-02-28 07:54:51'),
(8,2,1,'',NULL,'CASTILLOS PLANTA ALTA',NULL,NULL,'2020-04-01 14:44:55','2020-04-01 14:44:55','0.0000','0.0000',2,'0.0000','0.0000','0.0000',0,1,'2020-02-28 07:54:51'),
(9,2,1,'',NULL,'AZOTEA',NULL,NULL,'2020-04-01 14:44:55','2020-04-01 14:44:55','0.0000','0.0000',2,'0.0000','0.0000','0.0000',0,1,'2020-02-28 07:54:51'),
(10,2,1,'',NULL,'MUROS DE PATIO',NULL,NULL,'2020-04-01 14:44:55','2020-04-01 14:44:55','0.0000','0.0000',2,'0.0000','0.0000','0.0000',0,1,'2020-02-28 07:54:52'),
(11,2,1,'',NULL,'ALBAÑILERIA DE AZOTEA',NULL,NULL,'2020-04-01 14:44:55','2020-04-01 14:44:55','0.0000','0.0000',2,'0.0000','0.0000','0.0000',0,1,'2020-02-28 07:54:52'),
(12,2,1,'',NULL,'APLANADOS EXTERIORES',NULL,NULL,'2020-04-01 14:44:55','2020-04-01 14:44:55','0.0000','0.0000',2,'0.0000','0.0000','0.0000',0,1,'2020-02-28 07:54:52'),
(13,2,1,'',NULL,'APLANADO DE BAÑOS',NULL,NULL,'2020-04-01 14:44:55','2020-04-01 14:44:55','0.0000','0.0000',2,'0.0000','0.0000','0.0000',0,1,'2020-02-28 07:54:52'),
(14,2,1,'',NULL,'APLANADOS INTERIORES',NULL,NULL,'2020-04-01 14:44:55','2020-04-01 14:44:55','0.0000','0.0000',2,'0.0000','0.0000','0.0000',0,1,'2020-02-28 07:54:52'),
(15,2,1,'',NULL,'IMPERMEABILIZACION',NULL,NULL,'2020-04-01 14:44:55','2020-04-01 14:44:55','0.0000','0.0000',2,'0.0000','0.0000','0.0000',0,1,'2020-02-28 07:54:52'),
(16,2,1,'',NULL,'PISOS Y AZULEJOS',NULL,NULL,'2020-04-01 14:44:55','2020-04-01 14:44:55','0.0000','0.0000',2,'0.0000','0.0000','0.0000',0,1,'2020-02-28 07:54:52'),
(17,2,1,'',NULL,'FIRMES Y LAVADERO EN PATIO',NULL,NULL,'2020-04-01 14:44:55','2020-04-01 14:44:55','0.0000','0.0000',2,'0.0000','0.0000','0.0000',0,1,'2020-02-28 07:54:52'),
(18,2,1,'',NULL,'HUELLAS EN COCHERA',NULL,NULL,'2020-04-01 14:44:55','2020-04-01 14:44:55','0.0000','0.0000',2,'0.0000','0.0000','0.0000',0,1,'2020-02-28 07:54:52'),
(19,2,1,'',NULL,'CISTERNA',NULL,NULL,'2020-04-01 14:44:55','2020-04-01 14:44:55','0.0000','0.0000',2,'0.0000','0.0000','0.0000',0,1,'2020-02-28 07:54:52'),
(20,2,1,'',NULL,'MUEBLES DE BAÑO',NULL,NULL,'2020-04-01 14:44:55','2020-04-01 14:44:55','0.0000','0.0000',2,'0.0000','0.0000','0.0000',0,1,'2020-02-28 07:54:52'),
(21,2,1,'',NULL,'VENTANERIA DE ALUMINIO',NULL,NULL,'2020-04-01 14:44:55','2020-04-01 14:44:55','0.0000','0.0000',2,'0.0000','0.0000','0.0000',0,1,'2020-02-28 07:54:52'),
(22,2,1,'',NULL,'COLOCACION DE HERRERIA',NULL,NULL,'2020-04-01 14:44:55','2020-04-01 14:44:55','0.0000','0.0000',2,'0.0000','0.0000','0.0000',0,1,'2020-02-28 07:54:52'),
(23,2,1,'',NULL,'CABLEADO Y ACCESORIOS ELECTRICOS',NULL,NULL,'2020-04-01 14:44:55','2020-04-01 14:44:55','0.0000','0.0000',2,'0.0000','0.0000','0.0000',0,1,'2020-02-28 07:54:52'),
(24,2,1,'',NULL,'PINTURA',NULL,NULL,'2020-04-01 14:44:55','2020-04-01 14:44:55','0.0000','0.0000',2,'0.0000','0.0000','0.0000',0,1,'2020-02-28 07:54:52'),
(25,2,1,'',NULL,'EQUIPAMIENTO',NULL,NULL,'2020-04-01 14:44:55','2020-04-01 14:44:55','0.0000','0.0000',2,'0.0000','0.0000','0.0000',0,1,'2020-02-28 07:54:52'),
(26,2,1,'',NULL,'ENTREGA',NULL,NULL,'2020-04-01 14:44:55','2020-04-01 14:44:55','0.0000','0.0000',2,'0.0000','0.0000','0.0000',0,1,'2020-02-28 07:54:52');

UNLOCK TABLES;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

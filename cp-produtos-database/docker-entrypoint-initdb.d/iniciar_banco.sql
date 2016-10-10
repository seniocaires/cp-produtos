USE `cpprodutosdb`;

CREATE TABLE `pagina` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `link` varchar(800) NOT NULL,
  `numero` int NOT NULL,
  `dataAtualizacao` datetime NOT NULL,
  `tipo` varchar(15) NOT NULL,
  `html` text NULL,
  `ativa` char(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=latin1;
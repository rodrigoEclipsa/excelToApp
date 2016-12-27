# excelToApp
excelToApp es un API construida sobre Apache POI que permite reutilizar la logica de negocio construida con excel. posee soporte para múltiples libros y hojas, ademas se incorporaron funciones de alto nivel sobre el API que permite utilizar excel como un método para el desarrollo de software.

el API esta desarrollada con spark Framewok <a href="http://sparkjava.com/">spark</a> y maven. para utilizarlo solo deberia exportarlo a un archivo ejecutable .jar y enviar su solicitud por el puerto 4568.

el proyecto cuenta con un archivo de configuracion config.properties que posee dos propiedades
spreadsheetPath: es la ruta base en donde se almacenan las hojas de calculo
log4jPath: es la ruta hacia el archivo log4j.properties




# excelToApp
excelToApp es un API construida sobre Apache POI que permite reutilizar la logica de negocio construida con excel. posee soporte para múltiples libros y hojas, ademas se incorporaron funciones de alto nivel sobre el API que permite utilizar excel como un método para el desarrollo de software.
El API usa spark Framewok <a href="http://sparkjava.com/">spark</a> y maven. para utilizarlo solo deberia exportarlo a un archivo ejecutable .jar, ejecutarlo y enviar su solicitud por el puerto 4568.

# configuracion
El proyecto cuenta con un archivo de configuracion conf/config.properties que posee las propiedades
spreadsheetPath: es la ruta base en donde se almacenan las hojas de calculo.
<strong>log4jPath:</strong> es la ruta hacia el archivo log4j.properties, pude configurar log4j.properties como lo desee.
port: El puerto que utilizara el servicio por defecto es 4567.
El archivo conf/config.properties debe estar en el mismo directorio que el .jar, para exportarlo en eclipse file->export->runnable JAR file export. se recomienda usar la opcion "copy required libraries into a sub-folder next to the generated JAR"







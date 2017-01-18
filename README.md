# excelToApp
excelToApp es un API construida sobre Apache POI que permite reutilizar la logica de negocio construida con excel. 
posee soporte para múltiples libros y hojas, ademas se incorporaron funciones de alto nivel sobre el API que permite 
utilizar excel como un método para el desarrollo de software.
El API usa spark Framewok [spark](http://sparkjava.com/) y maven. para utilizarlo solo descargue el proyecto .jar, ejecutarlo y enviar su solicitud por el puerto 4568.

# configuracion
El proyecto cuenta con un archivo de configuracion conf/config.properties que posee las propiedades
* **spreadsheetPath:** es la ruta base en donde se almacenan las hojas de calculo.
* **log4jPath:** es la ruta hacia el archivo log4j.properties, pude configurar log4j.properties como lo desee.
* **port:** El puerto que utilizara el servicio por defecto es 4567.

*El archivo y el directorio conf/config.properties debe estar en el mismo directorio que el .jar ya que el código ira a buscar allí,
para exportarlo en eclipse file->export->runnable JAR file export. se recomienda usar la opción "copy required libraries into a 
sub-folder next to the generated JAR"*

#


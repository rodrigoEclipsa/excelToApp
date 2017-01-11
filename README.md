# excelToApp
excelToApp es un API construida sobre Apache POI que permite reutilizar la logica de negocio construida con excel para crear aplicaciones, posee soporte para m�ltiples libros y hojas, ademas se incorporaron funciones de alto nivel sobre el API que permiten ir mas all� de la resoluci�n de c�lculos.
El API usa spark Framewok [spark](http://sparkjava.com/) y maven. para utilizarlo solo descargue el proyecto .jar, ejecutarlo y enviar su solicitud por el puerto 4568.

![Arquitectura excelToApp](https://github.com/rodrigoEclipsa/excelToApp/blob/master/excelToApp%20API.png?raw=true)

## configuracion
El proyecto cuenta con un archivo de configuracion conf/config.properties que posee las propiedades
* **spreadsheetPath:** es la ruta base en donde se almacenan las hojas de calculo. estas deben estar almacenadas en un orden determinado para que la sistema trabaje con el archivo correcto, el orden es rootFolder/clientId/groupId/excel.xls donde rootFolder es *spreadsheetPath*, clientId es cualquier identificador de nuestro cliente(desarrollador excel) y groupId es cualquier identificador de la aplicaci�n que realizaremos. la url para la solicitud REST lucir� as� *midominio.com:port/calculate/clientId/groupappId*, de esta manera lograremos una estructura de directorio ordenada, tengan en cuenta que en muchas ocasiones una �nica aplicaci�n puede estar compuesta por m�ltiples hojas de calculo *clientId y groupId son identificadores cualquiera que usted desee asignar*
* **log4jPath:** es la ruta hacia el archivo log4j.properties, pude configurar log4j.properties como lo desee.
* **port:** El puerto que utilizara el servicio por defecto es 4567.

*El archivo y el directorio conf/config.properties debe estar en el mismo directorio que el .jar ya que el c�digo ira a buscar all�,
para exportarlo en eclipse file->export->runnable JAR file export. se recomienda usar la opci�n "copy required libraries into a 
sub-folder next to the generated JAR"*

## JSON I/O
La comunicacion con el servicio excelToApp se hace por medio de una solicitud REST al enpoint **midominio.com:port/calculate/clientId/groupappId** este endpoint recibe un JSON y responde un JSON que se ubica en el cuerpo de la solicitud, estos JSON tienen una estructura determinada y es muy importante conocerla ya que sera la �nica v�a de comunicaci�n con excelToApp, actualmente no poseemos librerias para la manipulacion de esta estructura por lo que solo hay dos opciones

1. crear los objetos manualmente y codificarlos en JSON
2. crear un archivo JSON definir las partes estaticas de la estructura decodificarlo en el codigo y agregar/quitar solo las partes dinamicas

Recomendamos la opci�n numero 2 ya que crear desde el c�digo toda la estructura puede resultar un trabajo agotador, tenga en cuenta que es una estructura anidada, seg�n nuestras practicas la opci�n numero 2 es la mejor por su velocidad de desarrollo y legibilidad en el c�digo. 

## Estructura simple del JSON input
La estructura del JSON que enviamos le dira a excelToApp que celdas queremos setear(cambiar el valor) y que celdas queremos recuperar, tambi�n se declaran funciones especiales como (dataTable,nInstances etc..)

un JSON simple podria lucir asi

     {
       "head":{
          "workBooks":[
             {
                "fileName":"simpleCalculate0.xlsx",
                "sheetsNames":[
                   "Hoja1",
                   "Hoja2"
                ]
             },
             {
                "fileName":"simpleCalculate1.xlsx",
                "sheetsNames":[
                   "Hoja1",
                   "Hoja2"
                ]
             }
          ]
       },
       "calculableVar":{
          "simpleCalculate0.xlsx!Hoja1!a1":1,
          "simpleCalculate0.xlsx!Hoja2!a1":2,
          "simpleCalculate1.xlsx!Hoja1!a1":5
       },
       "requestResult":[
          "simpleCalculate0.xlsx!Hoja1!a3",
          "simpleCalculate0.xlsx!Hoja2!a4",
          "simpleCalculate1.xlsx!Hoja1!a1"
       ]
    }

Analizamos propiedad por porpiedad

### head
Es un objeto que declararemos con que libros y hojas vamos a trabajar, estas se cargaran en memoria por lo que solamente declararemos solo aquellas que usaremos, tambi�n se tienen que incluir todos los archivos y hojas referenciados para poder satisfacer las dependencias. 

**workBooks**
Es un array de objetos en el cual cada objeto tiene 2 propiedades *fileName* el cual se establece el nombre del archivo y *sheetsNames* es un array en donde declaramos las hojas que usaremos de dicho archivo

### calculableVar
calculableVar es un objeto que contendra las celdas las cuales queremos cambiar el valor, cada propiedad se define asi *"nombrearchivo!nombredelahoja!celda":valor*

> cada ves que queramos hacer una referencia a una celda en excelToApp utilizaremos el mismo formato nombrearchivo!nombredelahoja!celda noten que el signo ! es solo un caracter separador

### requestResult
Aqui definimos las celdas que queremos obtener en el JSON de salida, y definimos las celdas siempre con el mismo formato
*"nombrearchivo!nombredelahoja!celda"*
Si queremos obtener todas las celdas de calculo(aquellas que tienen formula) podemos usar la siguiente sintexis *"nombrearchivo!nombredelahoja!\*"*
como ven se reemplaza el nombre de la celda por un *

## Estructura simple del JSON output
El JSON que nos devuelve excelToApp puede variar seg�n el JSON entrada pero por ejemplo en el JSON de entrada mensionado arriba lucira asi

    {
      "calculateResult": {
        "simpleCalculate0.xlsx!Hoja1!a3": valor,
        "simpleCalculate0.xlsx!Hoja2!a4": valor,
        "simpleCalculate1.xlsx!Hoja1!a1": valor
      }
    }
como ven el objeto calculateResult contiene el valor de las celdas que solicitamos en requestResult.

## Funciones especiales
Hasta aqu� hemos visto las funcionalidades esenciales de excelToApp que b�sicamente se trata de cambiar valores a celdas y obtener resultados, ahora veremos algunas funciones especiales que hace que excelToApp sea mas genial aun.

### Funcion dataTable
excelToApp no tiene soporte nativo para tabla de datos, esta es una carencia que arrastra apache POI y por lo tanto excelToApp, frente a esta situaci�n hemos a�adido la funcionalidad dataTable sobre el API la cual se manipula a trav�s del JSON de entrada como todo en excelToApp. un ejemplo de su uso es el siguiente

    "dataTable":[
       {
          "evaluateFormula":"flvarporcentual.xls!ppal!t68",
          "cellInput0":"flvarporcentual.xls!ppal!e7",
          "cellInput1":"flvarporcentual.xls!ppal!e9",
          "calculableVar":[
             {
                "input0":150,
                "input1":440,
                "setResult":"flvarporcentual.xls!ppal!u69"
             },
             {
                "input0":160,
                "input1":450,
                "setResult":"flvarporcentual.xls!ppal!u70"
             }
          ]
       }
    ]


**evaluateFormula:**  en esta propiedad indicaremos la celda la cual contiena la formula que usara la tabla de datos
 
 **cellInput0 y cellInput1:** en esta propiedad indicamos las celdas de entrada de la tabla, si es una sola entrada usaremos *cellInput0* si es de doble entrada usaremos ambas propiedades 
 
**calculableVar:** en este array indicaremos objetos, en cada objeto se establecer� que los valores para *cellInput0* y *cellInput1* con las propiedades *input0* y *input1*, tendremos un objeto por cada celda de la tabla a*setResult* le daremos el valor de la celda de la tabla que contiene el resultado, esta propiedad es solo para que excelToApp sepa que en el JSON de salida tiene que devolvernes dicha celda con su respectivo resultado


### Funcion nInstances
El prop�sito de la funci�n nInstances es poder dotar a excelToApp la capacidad para reutilizar la l�gica de negocio de excel aun cuando hay partes din�micas en la aplicaci�n.
imaginemos que tenemos un �rea en el excel en donde se resuelve determinado calculo(esta �rea la llamaremos componentes excel) y se requiere que la aplicaci�n tenga la capacidad de producir n instancias de ese componente, hasta aqu� todo es correcto ya que por mas instancias que halla desde nuestro c�digo siempre utilizaremos las mismas celdas para calcular, lo �nico que variara es lo que el usuario introduzca en cada instancia. pero ahora imaginemos que el desarrollador excel necesita conocer que variables y resultados se produjeron en cada instancia para poder realizar otros c�lculos. esta situaci�n aunque parezca rara en realidad no lo es y es muy probable que en alg�n momento surja esta necesidad, para seguir utilizando excelToApp en esta situaci�n tendr� que utilizar la funci�n *nInstances* que se declara de la siguiente forma

    "nInstances":[
       {
          "mapResultColumn":{
             "testNInstances.xlsx!Hoja1!i":"testNInstances.xlsx!Hoja1!c7"
          },
          "calculableVar":[
             {
                "testNInstances.xlsx!Hoja1!c4":1,
                "testNInstances.xlsx!Hoja1!c5":1,
                "testNInstances.xlsx!Hoja1!c6":1
             },
             {
                "testNInstances.xlsx!Hoja1!c4":2,
                "testNInstances.xlsx!Hoja1!c5":2,
                "testNInstances.xlsx!Hoja1!c6":3
             }
          ],
          "requestResult":[
             "testNInstances.xlsx!Hoja1!c7"
          ],
          "resultAfterNinstances":[
             "testNInstances.xlsx!Hoja1!f11"
          ]
       }
    ]

lo que hace inInstances a grandes rasgos es ubicar las celdas que necesitemos informar del componente excel en columnas, entonces por ejemplo si elegimos conservar la celda *a1* y elegimos la columna *z* podemos decir que *z1* sera el valor de *a1* de la primera instancia, *z2* sera el valor de *a1* de la segunda instancia... y as� sucesivamente, de esta forma el desarrollador excel tendr� a su disposici�n los valores que necesita para realizar su l�gica de negocio, es necesario mencionar que las columnas elegidas en este caso la columna *z* debe quedar reservada solo para el uso de la funcionalidad *nInstances* para implementar esta funcionalidad con �xito es necesario coordinar con el desarrollador excel.

**nInstances:** es un array de objetos en donde cada objeto representa la configuraci�n de cada componente excel

**mapResultColumn:** es un objeto en donde definimos la relaci�n que hay entre columna y celda, en otras palabras aqu� "mapearemos" las celdas en columnas. en el ejemplo se establece que en la columna *i* se almacenaran los valores de *c7*

**calculableVar:**es un array de objetos en donde cada objeto contiene los valores de las celdas variables(aquellas que el usuario cambia el valor) de cada instancia

**requestResult:**es un array en donde se declaran las celdas de calculo(aquellas que resuelven un calculo) que queremos obtener por cada instancia, se da por echo que en todas las instancias se necesitaran obtener las mismas celdas de calculo

**resultAfterNinstances:** es un array en donde se declaran las celdas que queremos obtener(generalmente celdas de calculo) despues de que se hallan mapeado todos los resultados en sus respectivas columnas, es decir que una ves mapeado todos los resultados excelToApp esta listo para obtener aquellas celdas que en sus calculos dependen de los valores de todas las instancias de los componentes
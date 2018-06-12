### Build and run

Инструкции по сборке:

Неплохой маленький пример
http://knowdimension.com/en/data/create-a-spark-application-with-scala-using-maven-on-intellij/

Мавеновские команды можно вызывать с помощью плагина в Идее, если в терминале не работает
``mvn clean package``

Чтобы работал `spark-submit`, нужно установить spark локально на компьютере, а не только через мавен
Само приложение можно запустить в терминале с помощью команды (нужно добавить свой путь до jar файла)
```
spark-submit --class cs500ir.spark.HelloSpark --master local /path/infosearch/target/spark-project-0.1-SNAPSHOT.jar
```

### Debug

Инструкцию, как запустить на дебаг, нашла тут: http://www.bigendiandata.com/2016-08-26-How-to-debug-remote-spark-jobs-with-IntelliJ/

Если коротко, то надо в консоли вызвать
```$xslt
export SPARK_SUBMIT_OPTS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=7777
spark-submit --class cs500ir.spark.HelloSpark --master local /path/infosearch/target/spark-project-0.1-SNAPSHOT.jar
``` 
А потом запустить remote debug с такими же параметрами
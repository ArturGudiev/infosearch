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

Как дебажить пока не уверена, добавляйте инфу, если получится запускать на дебаг.
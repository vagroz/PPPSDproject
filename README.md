# PPPSDproject
[![webserver_build_status](https://travis-ci.org/vagroz/PPPSDproject.svg?branch=master)](https://travis-ci.org/vagroz/PPPSDproject)
[![codecov](https://codecov.io/gh/vagroz/PPPSDproject/branch/master/graph/badge.svg)](https://codecov.io/gh/vagroz/PPPSDproject)



Веб-сервер и база данных, реализующие функционал [Trello](http://trello.com). Так как за сутки мало что можно успеть, имеются только три сущности:
* *Task* (id, name, description, listId) --- задание, которое прикрепляется к списку. Его можно создавать, удалять, смотреть по id и перемещать между листами.
* *List* (id, name, boardId) --- список, к которому прикрепляются задания. Каждый список прикреплен к доске. В данной  версии проекта можно только посмотреть существующие списки по названию доски.
* *Board* (id, name) --- доска (проект), к которой прикреплены списки.

### Чек-лист:
* [x] Система контроля версий.
* [x] ReadMe, из которого понятно, как запустить проект.
* [x] Лицензия.
* [x] Декларация сборки и зависимостей: SBT.
* [x] Статический анализ стиля кодирования:[scalastyle](http://www.scalastyle.org/sbt.html). Запуск: `sbt scalastyle`.
* [x] Непрерывная интеграция: [Travis](https://travis-ci.org/vagroz/PPPSDproject)
* [x] Юнит-тесты: [scalatest](http://www.scalatest.org/), [Akka HTTP Test Kit](https://doc.akka.io/docs/akka-http/10.0.11/scala/http/routing-dsl/testkit.html) + [sbt-scoverage](https://github.com/scoverage/sbt-scoverage) + [Codecove](https://codecov.io/gh/vagroz/PPPSDproject/branch/webservice)
* [x] Использование реляционной СУБД, причем схема БД должна быть в 3NF (если денормализацию нельзя аргументировать).
* [x] Веб-интерфейс и/или RPC API или REST API.
* [x] Сервис должен удовлетворять условиям масштабируемости из соответствующей лекции.
* [x] Реализована модель некоторой предметной области.
* [x] Документирующие комментарии — хотя бы для некоторого подмножества кодовой базы для демонстрации, например, для модели.

### Сборка и запуск:
Сборка в *jar* архив с включением всех зависимостей, в т.ч. библиотеку Scala:
```sbtshell
sbt assembly
```
Полученный архив лежит в `target/scala-2.12/pppsdproject.jar`. Запуск:
```
java -jar [CONFIGS] pppsdproject.jar [-t]
```
* ключ `-t` запускает сервер в тестовом режиме, без реальной БД.
* переменные *Java*-среды, например `-Dwebservice.endpoint=127.0.0.2` и `-Dwebservice.port=8085`, переопредеяют конфигурационные значения по умолчанию, заданные в файле `src/main/resources/application.conf`

### REST-API:
1. `/task/{id}`
* `GET`: получить задачу по id. Пример ответа:
    ```
    HTTP/1.1 200 OK
    {"status":"OK","payload":{"id":56,"name":"task56","listId":117}}
    ```
* `DELETE`: удалить задачу по id. Пример ответа:
    ```
    HTTP/1.1 204 No Content
    ```

* `PUT`, `?list={listName}`: перемещает задачу по id в указанный список.
Пример ответа:
    ```
    HTTP/1.1 204 No Content
    ```

2. `/task`
* `GET`, `?list={listName} & board={boardName}`: возвращает id задач, прикрепленных к указанному списку на указанной доске. Пример ответа:
    ```
    HTTP/1.1 200 OK
    {"status":"OK","payload":[1117,1118,1119]}
    ```
* `POST`: добавляет задачу. Принимает на вход *json* с описанием задачи, названиями доски и списка, к которым нужно это задание прикрепить. Пример входных данных:
    ```json
    {
      "task": {
        "name": "TaskName",
        "description": "Very important task"
      },
      "listName": "list1",
      "boardName": "board1"
    }
    ```
    В случае успеха возвращается id добавленной задачи. Пример ответа:
    ```
    HTTP/1.1 201 Created
    {"status":"OK","payload":1117}
    ```
3. `/list`
* `GET`, `?board={boardName}`: возвращает названия списков, прикрепленных к указанной доске. Пример ответа:
    ```
    HTTP/1.1 200 OK
    {"status":"OK","payload":["testList","testList1"]}
    ```
4. Обработка исключений. В проекте есть несколько различных кастомных исключений, на каждое выдается Http Response с соответствующим кодом и *json* с описанием ошибки. Например, при запросе несуществующего задания:
    ```
    HTTP/1.1 404 Not Found
    {"status":"Error","message":"Task with id=0 doesn't exist"}
    ```

### Использование СУБД
В итоге выбрана БД SQLite. Для работы с БД используется сторонняя библиотека, которая поддерживает и другие реляционные СУБД, поэтому переход делается сменой конфигов и зависимостей.
Таблицы описаны выше. Для демонстрации при первом запуске создаются пустые таблицы и заполняется доска (1, "InitialBoard"), в которой находятся списки (1, "BackLog", 1), (2, "In progress", 1), (3, "Testing", 1), (4, "Ready", 1), (5, "Release 1.0", 1).
Все id - primary key, автоинкрементируются. Также объявлены и foreign key с каскадным изменением.

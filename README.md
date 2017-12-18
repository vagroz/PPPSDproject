# PPPSDproject
Веб-сервер и база данных, реализующие функционал [Trello](http://trello.com). Так как за сутки мало что можно успеть, имеются только три сущности:
* *Task* (id, name, description, listId) --- задание, которое прикрепляется к списку. Его можно создавать, удалять, смотреть по id и перемещать между листами.
* *List* (id, name, boardId) --- список, к которому прикрепляются задания. Каждый список прикреплен к доске. В данной  версии проекта можно только посмотреть существующие списки по названию доски.
* *Board* (id, name) --- доска (проект), к которой прикреплены списки.

### Чек-лист:
* [x] Система контроля версий.
* [x] ReadMe, из которого понятно, как запустить проект.
* [x] Лицензия.
* [x] Декларация сборки и зависимостей: SBT.
* [ ] Статический анализ стиля кодирования: [scalastyle](http://www.scalastyle.org/sbt.html). Запуск: `sbt scalastyle`.
* [ ] Непрерывная интеграция.
* [ ] Юнит-тесты.
* [ ] Использование реляционной СУБД, причем схема БД должна быть в 3NF (если денормализацию нельзя аргументировать).
* [x] Веб-интерфейс и/или RPC API или REST API.
* [x] Сервис должен удовлетворять условиям масштабируемости из соответствующей лекции.
* [x] Реализована модель некоторой предметной области.
* [x] Документирующие комментарии — хотя бы для некоторого подмножества кодовой базы для демонстрации, например, для модели.

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
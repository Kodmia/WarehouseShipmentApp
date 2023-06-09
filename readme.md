# Warehouse Shipment
Приложение для фотофиксации собранных заказов перед отправкой транспортными компаниями. Сервер для приложения доступен по ссылке [Warehouse Shipment Server](https://github.com/Kodmia/WarehouseShipmentServer)
## Возможности
- Аутентификация пользователя с использованием JWT.
- Вывод информации обо всех отправленных заказах.
- Врзможность просмотра информации о времени сборки, сборщике и приложенных фото.
- Сохранение изображений собранного заказа.
- Возможность оставить комментарий о заказе.
- Получение информации о составе заказе из внутренней системы (1С или какой-нибудь другой сервис).
- Возможность подсветить "физическое" место хранения для быстрого поиска продукта для упрощения поиска товара.

<img src="/screenshots/s1.png" width="200"> <img src="/screenshots/s2.png" width="200"> <img src="/screenshots/s3.png" width="200"> <img src="/screenshots/s4.png" width="200"> <img src="/screenshots/s5.png" width="200">

## Использованные технологии
- Jetpack Navigation
- WorkManager
- ViewPager 2
- Retrofit
- Glide
- CameraX, Google ML Kit

## Использование
- Аутентификация на стартовом экране с ипользованием выдаваемой администратором пары "логин - пароль".
- Просмотр собранных заказов или добавление нового.
- Для добавления нового заказа необходимо отсканировать его QR-код или ввести его название вручную.
- Отметить собранные позиции. 
- При необходимости, "подсветить" товар, нажав на его название. Добавить от 1 до 3 фото собранного заказа. Добавить комментарий, при необходимости.
- Отправить собранный заказ на сервер.

## Настройка
Перед использоdаниеv приложения необходимо указать адрес сервера [Warehouse Shipment Server](https://github.com/Kodmia/WarehouseShipmentServer) в файле `gradle.properties`
```sh
DebugServerBaseUrl="http://<server_ip:port>"
ServerBaseUrl="http://<server_ip:port>"
```
после этого можно собрать проект.

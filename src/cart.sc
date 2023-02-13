require: function.js

theme: /

    state: Cart
        intent!: /корзина
        a: Ваша корзина:
        script:
            $temp.totalSum = 0;
            var n = 0;
            for(var i = 0; i < $session.cart.length; i++){
                var current_position = $session.cart[i];
                for(var id = 1; id < Object.keys(flowers).length + 1; id++){
                    if (current_position.name == flowers[id].value.title){
                        var variation = _.find(flowers[id].value.variations, function(variation){
                            return variation.id === current_position.id;
                        });

                        n++;

                        if (!variation) {
                            $reactions.answer("Что-то пошло не так, вариант не найден для id " + current_position.id);
                        } else {
                            $reactions.answer(n + ". " + current_position.name + ", " + variation.name + "\nЦена: " + variation.price + "\nКоличество: " + current_position.quantity);

                            $reactions.inlineButtons({text: "Удалить", callback_data: current_position.name});

                            $temp.totalSum += variation.price * current_position.quantity;
                        }
                    }
                }
            }
            $session.messageId = $request.rawRequest.message.message_id + n + 2;
            
        state: WriteDataToCells 
            q!: WriteDataToCells 
            
            script:
                var phone = $client.phone_number 
                var city =  $client.city
                var sum = $temp.totalSum
                var name = $request.userFrom.firstName
                
                GoogleSheets:
                    operationType = writeDataToLine
                    integrationId = 0d4705f-6d44-4b46-b3ae-ad5fcdd48401
                    spreadsheetId = 1QnCnl_asxbkzpb7OP6W1Y-J9DIRSIQmxzYIP-J02WRQ
                    sheetName = Orders
                    body =
                        [
                           { "values": [name, phone, city, $session.cart.name[0], $session.cart.quantity[0], $session.cart.name[1], $session.cart.quantity[1],
                                $session.cart.name[2], $session.cart.quantity[2], sum]
                           }    
                        ]

        a: Общая сумма заказа: {{ $temp.totalSum }} рублей.
        a: Если все верно, отправьте свой номер телефона, и наш менеджер с вами свяжется.
        buttons:
            {text: "Отправить номер телефона", request_contact: true}
            "Меню" -> /ChooseFlowers
 
        state: Edit
            event: telegramCallbackQuery
            script:
                var name = $request.rawRequest.callback_query.data;
                deleteFromCart(name);
                var message_id = $request.rawRequest.callback_query.message.message_id;

                editText(message_id, 'Удален');
                editText($session.messageId, 'Общая сумма заказа: ' + getTotalSum() + ' руб.');
            if: $session.cart.length == 0
                a: Вы очистили корзину
                go!: /ChooseFlowers

            state: ClickButtons
                q: *
                a: Нажмите, пожалуйста, кнопку.
                go!: ..

    state: GetPhoneNumber
        event: telegramSendContact
        script:
            $client.phone_number = $request.rawRequest.message.contact.phone_number;
            
                
      
        state: SendOrder
            q!: SendOrder
            script:
                #Данные для менеджера в Телеграмм
                var phone = $client.phone_number 
                var city =  $client.city
                var name = $request.userFrom.firstName
                var link = https://api.telegram.org/5853333291:AAGPTVcZHSB8OZbY2gR5u7J7Srpx9U-f0og/getMe
                
                $http.post(link, {
                timeout: 10000,
                fileUrl: 'https://example_path',     // путь до файла
                fileName: 'filename.png'             // имя файла
            });
                
                
                
                    
        a: Спасибо! Наш менеджер свяжется с вами по номеру телефона {{ $client.phone_number }}.
        
    
    

    
    

require: flowers.sc

theme: /

    state: Start
        q!: $regex</start>
        script:
            $context.session = {};
            $context.client = {};
            $context.temp = {};
            $context.response = {};
        a: Привет! Я электронный помощник в магазине цветов. Могу помочь вам сделать заказ.
        go!: /ChooseCity

    state: ChooseCity || modal = true
        a: Выберите свой город.
        buttons:
            "Санкт-Петербург" -> ./RememberCity
            "Москва" -> ./RememberCity

        state: RememberCity
            script:
                $client.city = $request.query;
                $session.cart = [];
            go!: /ChooseFlowers

        state: ClickButtons
            q: *
            a: Нажмите, пожалуйста, кнопку.
            go!: ..

    state: CatchAll || noContext=true
        event!: noMatch
        a: Я вас не понимаю.

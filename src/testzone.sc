theme: /
    #стейт для сброса клиентских и сессионных переменных
    state: Reset
        q!: reset
        a: сбросил клиентские и сессионные переменные
        script:
            $client = {};
            $session = {};
    
    #стейт чтобы увидеть все действующие клиентские переменные
    state: ClientInfo
        q!: покажи клиентские
        script: $reactions.answer(toPrettyString($client));
    
    state: myData
        q!: покажи меня
        script:
            $reactions.answer($client.id);
            $reactions.answer($client.username);

    state: file
        event!: noMatch 
        event!: fileEvent
        event!: telegramAnyMessage
        event!: telegramApiRequestFailed
        a: получил
        # a: Привет {{$request.rawRequest.message.video.file_id}}
        # a: data {{toPrettyString($request.rawRequest)}}
        script:
            # $reactions.answer("$Context");
            # $reactions.answer(toPrettyString($Context));
            $reactions.answer("$request");
            $reactions.answer(toPrettyString($request));
            # $reactions.answer("$response");
            # $reactions.answer(toPrettyString($response));
    
    state: testStart
        q!: тестовый старт
        # script: $session.timeoutTime = 30
        go!: /Start
        